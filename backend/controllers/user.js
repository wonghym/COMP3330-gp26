const bcrypt = require("bcrypt");
const userRouter = require("express").Router();
const User = require("../models/user");

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

  const savedUser = await user.save();

  response.status(201).end();
});

module.exports = userRouter;
