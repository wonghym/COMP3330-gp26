const mongoose = require("mongoose");

const postSchema = new mongoose.Schema({
  date: Date,
  lastMsg: {
    type: Date,
    default: new Date(),
  },
  title: {
    type: String,
    required: true,
  },
  content: {
    type: String,
    required: true,
  },
  hashtags: Array,
  curstat: {
    type: Number,
    default: 0,
  },
  maxstat: {
    type: Number,
    default: 0,
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  joinedUser: [
    {
      user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
      },
      notiCount: {
        type: Number,
        default: 0,
      },
      _id: false,
    },
  ],
  msg: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Forum",
    },
  ],
  hidename: {
    type: Boolean,
    default: false,
  },
});

postSchema.set("toJSON", {
  transform: (document, returnedObject) => {
    returnedObject.id = returnedObject._id.toString();
    delete returnedObject._id;
    delete returnedObject.__v;
  },
});

const Post = mongoose.model("Post", postSchema);

module.exports = Post;
