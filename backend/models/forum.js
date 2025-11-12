const mongoose = require("mongoose");

const forumSchema = new mongoose.Schema({
  date: Date,
  content: {
    type: String,
    required: true,
    maxLength: 200,
  },
  like: String,
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  post: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Post",
  },
});

forumSchema.set("toJSON", {
  transform: (document, returnedObject) => {
    returnedObject.id = returnedObject._id.toString();
    delete returnedObject._id;
    delete returnedObject.__v;
  },
});

const Forum = mongoose.model("Forum", forumSchema);

module.exports = Forum;
