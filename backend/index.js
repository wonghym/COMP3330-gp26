const express = require("express");
const mongoose = require("mongoose");
const morgan = require("morgan");
const config = require("./utils/config");
const logger = require("./utils/logger");
const middlewares = require("./utils/middleware");
const postRouter = require("./controllers/post");
const userRouter = require("./controllers/user");
const loginRouter = require("./controllers/login");

const app = express();

app.use(express.json());
app.use(
  morgan(":method :url :status :res[content-length] - :response-time ms")
);

const mongoUrl = config.MONGODB_URI;
mongoose.connect(mongoUrl).then(logger.info("mongodb conencted"));

app.use("/api/post", postRouter);
app.use("/api/user", userRouter);
app.use("/api/login", loginRouter);

app.use(middlewares.errorHandler);
app.use(middlewares.unknownEndpoint);

app.listen(config.PORT, () => {
  logger.info(`Server running on port ${config.PORT}`);
});
