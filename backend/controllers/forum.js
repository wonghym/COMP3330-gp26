const forumRouter = require("express").Router();
const Post = require("../models/post");
const User = require("../models/user");
const Forum = require("../models/forum");

forumRouter.get("/", async (request, response) => {
  const msgs = await Forum.find({})
    .populate({ path: "user", select: "id name" })
    .populate({ path: "post", select: "id title" });
  response.json(msgs);
});

// get all messages of post
forumRouter.get("/:id", async (request, response) => {
  const postId = request.params.id;
  try {
    const targpost = await Post.findById(postId)
      .sort({ date: 1 })
      .populate({
        path: "msg",
        select: "date content id user",
        populate: {
          path: "user",
          select: "name",
        },
      });
    response.json(targpost.msg);
  } catch {
    response.status(404).json({ error: "Post not found." });
  }
});

forumRouter.post("/", async (request, response) => {
  const user = await User.findById(request.body.user);
  const targpost = await Post.findById(request.body.post);

  if (!user || !targpost) {
    return response.status(400).json({
      error: "userId/postId missing or not valid",
    });
  }

  const msg = Forum({ ...request.body, date: new Date() });
  const savedMsg = await msg.save();
  targpost.msg = targpost.msg.concat(savedMsg._id);
  await targpost.save();

  response.status(201).json(savedMsg).end();
});

module.exports = forumRouter;
