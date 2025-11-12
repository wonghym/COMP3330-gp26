const bcrypt = require("bcrypt");
const userRouter = require("express").Router();
const User = require("../models/user");
const Post = require("../models/post");

userRouter.get("/", async (request, response) => {
  const users = await User.find({}).populate("posts").populate("joinedPost");
  response.json(users);
});

userRouter.get("/:id", async (request, response) => {
  const userId = request.params.id;
  try {
    const user = await User.findById(userId);

    if (!user) {
      return response.status(404).json({ error: "User not found." });
    }

    return response.json(user);
  } catch (error) {
    console.log("Error fetching user", error);
    return response.status(500).json({
      error: "Server Error",
      details: error.message,
    });
  }
});

userRouter.post("/", async (request, response) => {
  const { username, name, password } = request.body;

  if (!username) {
    return response.status(400).json({ error: "Missing username" });
  }

  if (!password) {
    return response.status(400).json({ error: "Missing password" });
  }

  if (password.length < 3) {
    return response
      .status(400)
      .json({ error: "Password should be at least 3 letters long" });
  }

  const saltRounds = 10;
  const passwordHash = await bcrypt.hash(password, saltRounds);

  const user = User({
    username,
    name,
    bio: "",
    passwordHash,
  });

  try {
    const savedUser = await user.save();
    response.status(201).json(savedUser);
  } catch (error) {
    if (error.code === 11000) {
      return response.status(400).json({
        error: "Username is already taken",
      });
    }

    return response.status(500).json({
      error,
    });
  }
});

userRouter.put("/:id", async (request, response) => {
  const updatedUser = await User.findByIdAndUpdate(
    request.params.id,
    { ...request.body },
    { new: true }
  );
  response.status(201).json(updatedUser).end;
});

userRouter.delete("/:id", async (request, response) => {
  const userId = request.params.id;
  const user = await User.findByIdAndDelete(userId);

  if (!user) {
    return response.status(404).json({ error: "User not found." });
  }

  if (user.posts && user.posts.length > 0) {
    const postsToDelete = user.posts;
    await User.updateMany(
      { joinedPost: { $in: postsToDelete } },
      { $pull: { joinedPost: { $in: postsToDelete } } }
    );

    await Post.deleteMany({ _id: { $in: postsToDelete } });
  }

  if (user.joinedPost && user.joinedPost.length > 0) {
    await Post.updateMany(
      { _id: { $in: user.joinedPost } },
      { $pull: { joinedUser: userId } }
    );
  }

  response.status(204).end();
});
module.exports = userRouter;
