const postRouter = require("express").Router();
const Forum = require("../models/forum");
const Post = require("../models/post");
const User = require("../models/user");

postRouter.get("/", async (request, response) => {
  const posts = await Post.find({})
    .populate({ path: "user", select: "username name id" })
    .populate({ path: "joinedUser", select: "id" })
    .populate({ path: "msg", select: "content like user" });
  response.json(posts);
});

postRouter.post("/", async (request, response) => {
  const user = await User.findById(request.body.user);

  if (!user) {
    return response.status(400).json({
      error: "userId missing or not valid",
    });
  }

  const post = Post({ ...request.body, date: new Date() });
  const savedPost = await post.save();
  user.posts = user.posts.concat(savedPost._id);
  await user.save();

  response.status(201).json(savedPost).end();
});

postRouter.delete("/:id", async (request, response) => {
  const postId = request.params.id;

  const post = await Post.findByIdAndDelete(postId);

  if (!post) {
    return response.status(404).json({ error: "Post not found." });
  }

  await Forum.deleteMany({ post: postId });

  if (post.user) {
    await User.findByIdAndUpdate(post.user, { $pull: { posts: postId } });
  }

  if (post.joinedUser && post.joinedUser.length > 0) {
    await User.updateMany(
      { _id: { $in: post.joinedUser } },
      { $pull: { joinedPost: postId } }
    );
  }

  response.status(204).end();
});

module.exports = postRouter;
