const mongoose = require("mongoose");

const postSchema = new mongoose.Schema({
  date: Date,
  title: {
    type: String,
    required: true,
  },
  content: {
    type: String,
    required: true,
  },
  hashtags: Array,
  curstat: String,
  maxstat: String,
  like: String,
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  joinedUser: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
    },
  ],
  msg: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Forum",
    },
  ],
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
