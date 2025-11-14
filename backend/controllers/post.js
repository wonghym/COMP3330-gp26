const postRouter = require("express").Router();
const mongoose = require("mongoose");
const Forum = require("../models/forum");
const Post = require("../models/post");
const User = require("../models/user");

postRouter.get("/", async (request, response) => {
  const posts = await Post.find({}).populate({
    path: "user",
    select: "username name id",
  });
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
  post.joinedUser = post.joinedUser.concat({ user: user._id, notiCount: 0 });
  const savedPost = await post.save();
  user.posts = user.posts.concat(savedPost._id);
  user.joinedPost = user.joinedPost.concat(savedPost._id);
  await user.save();

  response.status(201).json(savedPost).end();
});

postRouter.put("/join", async (request, response) => {
  const { user: userId, post: postId } = request.body;

  if (!userId || !postId) {
    return response.status(400).json({
      error: "userId and postId are required",
    });
  }

  const session = await mongoose.startSession();

  try {
    session.startTransaction();
    const post = await Post.findById(postId).session(session);
    if (!post) {
      throw new Error("Post not found");
    }

    const user = await User.findById(userId).session(session);
    if (!user) {
      throw new Error("User not found");
    }

    if (post.joinedUser.some((entry) => entry.user.equals(userId))) {
      throw new Error("User has already joined this post");
    }

    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { $addToSet: { joinedPost: postId } },
      { new: true, session: session }
    );

    const updatedPost = await Post.findByIdAndUpdate(
      postId,
      {
        $inc: { curstat: 1 },
        $push: {
          joinedUser: { user: userId, notiCount: 0 },
        },
      },
      { new: true, session: session }
    );

    await session.commitTransaction();

    response.status(200).json({ user: updatedUser, post: updatedPost });
  } catch (error) {
    await session.abortTransaction();
    console.error("Transaction aborted:", error.message);

    if (
      error.message === "User not found" ||
      error.message === "Post not found"
    ) {
      return response.status(404).json({ error: error.message });
    }
    if (error.message === "User has already joined this post") {
      return response.status(400).json({ error: error.message });
    }

    response.status(500).json({ error: "An internal server error occurred" });
  } finally {
    session.endSession();
  }
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
      { _id: { $in: post.joinedUser.user } },
      { $pull: { joinedPost: postId } }
    );
  }

  response.status(204).end();
});

module.exports = postRouter;
