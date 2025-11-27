<h1>
Fall 2025
COMP3330 Group Project - gp26 <br />
Member: Matthew Wong <br />
</h1>

<h2>Installation</h2>
open the code file in android studio

backend:
In terminal

```
cd backend
npm install
npm run dev
```

frontend - mobile application:<br />
Click the start button at the top tool bar to start the frontend

<h2>Important (if cloned from github):</h2>
The `.env` file is not included in the repository. To run the backend:

1. Create a `.env` file in the `backend/` root directory.
2. Add the variables `MONGODB_URI` and `SECRET` (for JWT, any string will suffice).

   Example content for `.env`:

   ```env
   MONGODB_URI=your_mongodb_connection_string
   SECRET=your_jwt_secret_key (anything)
   ```

<h2>How to use the app:</h2>
This app consists of 3 parts, forum, chatroom and profile.

Forum:

- Clicking into the post will show the post description, and sub-message thread
- Join button in post description page, indicates that user has joined post owner's group
- The blue floating button at the right hand corner will direct you to add post/message page
- Add post/message page should be self explanatory, and title/description field is mandatory
- Clicking profile picture of users (not anonymous) directs the user's profile page

Chatroom:

- Contains the chatrooms of user created posts/joined groups
- Self explanatory, works like whatsapp

Profile:

- If not logged in and does not have an account, you can sign up for an account, or use sample accounts below (username is for logging in, name is for displaying to public)
- If logged in, user can see profile page, allowing the change of profile picture/displayed name/bio

Sample accounts:
| username | password |
| :--- | :--- |
| alice | alice |
| bob | bob |
| eve | eve |

<h2>Final words:</h2>
This project is done by one person within a limited timeframe, so there may not be much to be delivered. Still appreciated for feedback if possible. Please enjoy the mobile app.
