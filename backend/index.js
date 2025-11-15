const express = require("express");
const mongoose = require("mongoose");
const morgan = require("morgan");
const config = require("./utils/config");
const logger = require("./utils/logger");
const middlewares = require("./utils/middleware");
const postRouter = require("./controllers/post");
const userRouter = require("./controllers/user");
const loginRouter = require("./controllers/login");
const forumRouter = require("./controllers/forum");
const chatroomRouter = require("./controllers/chatroom");

const app = express();

app.use(express.json({ limit: "50mb" }));
app.use(express.urlencoded({ limit: "50mb", extended: true }));
app.use(
  morgan(":method :url :status :res[content-length] - :response-time ms")
);

const mongoUrl = config.MONGODB_URI;
mongoose.connect(mongoUrl).then(logger.info("mongodb conencted"));

app.use("/api/post", postRouter);
app.use("/api/user", userRouter);
app.use("/api/login", loginRouter);
app.use("/api/forum", forumRouter);
app.use("/api/chat", chatroomRouter);

app.use(middlewares.errorHandler);
app.use(middlewares.unknownEndpoint);

app.listen(config.PORT, (host = "0.0.0.0"), () => {
  logger.info(`Server running on port ${config.PORT}`);
});
