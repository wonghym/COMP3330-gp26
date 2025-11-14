const Chatroom = require("../models/chatroom");
const Post = require("../models/post");

const chatroomRouter = require("express").Router();

chatroomRouter.get("/", async (request, response) => {
  const chatrooms = await Chatroom.find({})
    .populate({ path: "sender", select: "name id" })
    .populate({ path: "post", select: "title id joinedUser" });
  response.json(chatrooms);
});

chatroomRouter.get("/post/:id", async (request, response) => {
  const chatrooms = await Chatroom.find({ post: request.params.id })
    .sort({ time: 1 })
    .populate({ path: "sender", select: "name id" });

  response.json(chatrooms);
});

chatroomRouter.get("/user/:id", async (request, response) => {
  try {
    const userId = request.params.id;
    const posts = await Post.find({ "joinedUser.user": userId })
      .select("title id lastMsg joinedUser")
      .sort({ lastMsg: 1 });

    const transformedPosts = posts.map((post) => {
      const userEntry = post.joinedUser.find((entry) =>
        entry.user.equals(userId)
      );
      const notiCount = userEntry ? userEntry.notiCount : 0;

      return {
        id: post.id,
        title: post.title,
        lastMsg: post.lastMsg,
        notiCount: notiCount,
      };
    });

    response.json(transformedPosts);
  } catch (error) {
    response.status(500).json({ error: "Server Error" });
  }
});

module.exports = chatroomRouter;
