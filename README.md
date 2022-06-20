# Comic Convo

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Keeps track of users' likes/dislikes with the comics they read and matches them with others to trade and talk about comics. Can be used to meet others while enjoying your favorite heroes!

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social networking
- **Mobile:** App will be made for mobile but may be applicable to a web environment. Mobile version may look smoother or have swiping feature.
- **Story:** User can list out their comic preferences and is matched with others with similar preferences. Users can then message these people and keep a log of their conversations.
- **Market:** Any individual could use this app, but there could be a kid-friendly mode that would exclude certain features.
- **Habit:** This app could be used whenever the user likes, depending on how often they want to meet others
- **Scope:** The app could start as a simple app in the user's area, but it could be broadened to include sharing of comics internationally through mail.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can log to access their chats and preferences
* User can create a list of their comic favorites and dislikes
* User can get matched to people in the area with similar preferences and can chat through messages
* Each user has a profile page with basic info and their comic preferences
* Settings (Accesibility, Notification, General, etc.)
* Map feature where users can choose where to meet up or point out where to find cool comic finds.

**Optional Nice-to-have Stories**

* Page with stats of things like users' most read about comic character or least favorite comic series.
* Community page where users can post about good comic finds in their area or to buy/sell comics
* Allow people to connect their social media accounts
* Kid-friendly setting that blocks out certain information and can only be used by same age group

### 2. Screen Archetypes

* Login
* Register - user signs up or logs in to preexisting account
* Messages - 1 on 1 communication between users
    * User can select to message another user through their profile page or when they are matched
* Comic preference screen
    * Users can search for comics and put them in their like/dislike section
* Settings
    * Lets people change notification, language, and potentially age settings

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Map
* Profile
* Matches
* Messages
* Settings

**Flow Navigation** (Screen to Screen)

* Log-in -> Account registration if no account is available
* Profile -> Text field to be modified -> Comic search page to add comics to likes/dislikes
* Matches -> Messages if chosen
* Messages -> Map for meetup if desired
* Maps -> Add pin
* Settings -> Toggle settings

## Wireframes

![CapstoneWireframe](https://user-images.githubusercontent.com/74567614/173668117-6e14f91b-3a01-40b4-b582-fbc6093bccaa.jpg)

## Schema

### Models
#### Message

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user message (default field) |
   | author        | Pointer to User| message author |
   | body          | String   | body of message |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | image         | File     | profile image |
   | email         | String   | user email |
   | password      | String   | user password |
   | name          | String   | user name |
   | description   | String   | about me description of user |
   | comicLikes    | Array of comics   | comics added to favorites list by users |
   | comicDislikes | Array of comics   | comics added to favorites list by users |
   | createdAt     | DateTime | date when user is created (default field) |
   | updatedAt     | DateTime | date when user is last updated (default field) |
   
#### Pin

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the pin (default field) |
   | image         | File     | image attached to map pin |
   | author        | Pointer to User| pin author |
   | description   | String   | caption attatched to the pin |
   | createdAt     | DateTime | date when pin is created (default field) |
   | updatedAt     | DateTime | date when pin is last updated (default field) |
   
#### Comic

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the comic (default field) |
   | image         | File     | image of comic cover |
   | author        | String   | author of comic |
   | title         | String   | title of comic |
   | characters    | Array of Strings | characters included in the comic
   | createdAt     | DateTime | date when pin is created (default field) |
   | updatedAt     | DateTime | date when pin is last updated (default field) |
   
   
### Networking
#### List of network requests by screen
   - Matches
      - (Read/GET) Query matched user object
   - Map
      - (Read/GET) Query all pins
      ```java
         ParseQuery<Pin> query = ParseQuery.getQuery(Pin.class);
         query.include("user");
         query.findInBackground(new FindCallback<Pin>() {
            @Override
            public void done(List<Pin> pins, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting pins", e);
                    return;
                }
                Log.i(TAG, "Successfully retrieved pins");
                //Do something with the pins
            }
        });
         ```
      - (Create/POST) Create a new pin object
      - (Delete) Delete existing pin
   - Message Screen
      - (Read/GET) Query all messages between user and match
      - (Create/POST) Create a new message object
      - (Delete) Delete existing message
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image

#### Existing API Endpoints
##### Marvel Comics API
- Base URL - [https://developer.marvel.com/](https://developer.marvel.com/)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /v1/public/comics/{comicId}/characters | get list of characters filtered by comic id
    `GET`    | /v1/public/comics | return list of comics
    `GET`    | /v1/public/comics?characters=characterId  | get comics that contain specific characters characters
    `GET`    | /v1/public/comics?title=title | get comics that match given title
