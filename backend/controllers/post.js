const postRouter = require("express").Router();
const Post = require("../models/post");
const User = require("../models/user");

postRouter.get("/", async (request, response) => {
  const posts = await Post.find({})
    .populate({ path: "user", select: "username name id" })
    .populate({ path: "joinedUser", select: "username name id" });
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

module.exports = postRouter;
