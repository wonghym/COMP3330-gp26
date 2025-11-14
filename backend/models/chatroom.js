const mongoose = require("mongoose");

const chatroomSchema = new mongoose.Schema({
  content: {
    type: String,
    required: true,
  },
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
    require: true,
  },
  post: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Post",
    required: true,
  },
  time: {
    type: Date,
    default: new Date(),
  },
});

chatroomSchema.set("toJSON", {
  transform: (document, returnedObject) => {
    returnedObject.id = returnedObject._id.toString();
    delete returnedObject._id;
    delete returnedObject.__v;
  },
});

chatroomSchema.index({ post: 1, time: 1 });

const Chatroom = mongoose.model("Chatroom", chatroomSchema);

module.exports = Chatroom;
