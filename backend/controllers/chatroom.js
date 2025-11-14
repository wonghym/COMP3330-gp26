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

chatroomRouter.post("/", async (request, response) => {
  const { postId, userId, content } = request.body;

  if (!postId || !userId || !content) {
    return response.status(400).json({
      error: "Missing postId / userId / content",
    });
  }

  try {
    const msg = new Chatroom({
      content: content,
      sender: userId,
      post: postId,
    });
    const newMsg = await msg.save();
    await Post.findByIdAndUpdate(postId, {
      $push: { msg: newMsg._id },
      $set: { lastMsg: new Date() },
    }).exec();

    await Post.updateOne(
      { _id: postId },
      { $inc: { "joinedUser.$[elem].notiCount": 1 } },
      { arrayFilters: [{ "elem.user": { $ne: userId } }] }
    ).exec();

    await Post.updateOne(
      { _id: postId },
      { $set: { "joinedUser.$[elem].notiCount": 0 } },
      { arrayFilters: [{ "elem.user": userId }] }
    ).exec();

    const populatedMessage = await Chatroom.findById(newMsg._id).populate(
      "sender",
      "name"
    );
    const senderData = populatedMessage.sender;

    response.status(201).json({
      content: populatedMessage.content,
      time: populatedMessage.time,
      id: populatedMessage._id,
      post: populatedMessage.post,
      sender: {
        id: senderData._id,
        name: senderData.name,
      },
    });
  } catch (error) {
    console.log(error);
    response.status(500).json({ error: "Server error" });
  }
});

module.exports = chatroomRouter;
