# AsanaBot 
[![Circle CI](https://circleci.com/gh/Financial-Times/asana-bot.svg?style=svg&circle-token=de0b7c29ace590cf37f1e1bf0600dff2fa9c6035)](https://circleci.com/gh/Financial-Times/asana-bot)

A [bot](http://en.wikipedia.org/wiki/Internet_bot) for use with [Asana](http://asana.com).
 
When tasks are assigned to Asana users which the bot runs, it adds tasks assigned to these users to 
corresponding projects (for example the GraphicsProject) and also puts a comment on the task to highlight 
it has been added when done.

* GraphicsBot -> when assigned adds tasks to the Graphics project
* PicturesBot -> when assigned adds tasks to the Pictures project
* ReportBot -> when assigned backs up the project
* SocialBot -> when assigned adds tasks to the Social Promotion Requests project
* InteractivesBot -> when assigned adds tasks to the Interactive Requests project
* VideoBot -> when assigned adds tasks to the Video Request project

## Setup

Below environment variables must be set in order for the bot to be able to communicate with:

* ASANA_GRAPHICS_KEY
* ASANA_PICTURES_KEY
* ASANA_REPORT_KEY
* ASANA_SOCIAL_KEY
* ASANA_INTERACTIVES_KEY
* ASANA_VIDEO_KEY

These need to be set with the corresponding [api](https://asana.com/guide/help/api/api) key of the user which the tasks will be moved as. 
This user needs to have access to projects where tasks are being assigned and also the projects where tasks are being added. 

on linux/mac these can be set with the cmds:

`export ASANA_GRAPHICS_KEY=[API KEY]`

Below environment variables must be set in order for the app to authenticate user against Google OAuth2:
* OAUTH_CLIENT_ID
* OAUTH_CLIENT_SECRET

Finally backup properties are driven by following variables:
* GOOGLE_API_PRIVATE_KEY_ID
* GOOGLE_API_PRIVATE_KEY
* GOOGLE_API_PRIVATE_CLIENT_EMAIL
* GOOGLE_API_PRIVATE_CLIENT_ID

## Running locally

This project is built on spring boot and to run locally just run the command:

`mvn spring-boot:run`

## Deploying to Heroku and using CircleCI

Two config files are included:

* Procfile for [Heroku](http://docs.spring.io/spring-boot/docs/current/reference/html/cloud-deployment-heroku.html) deployment.
* circle.yml for [CircleCI](http://circleci.com) continous integration/deployment configuration. 

The Heroku instance will also need the system.properties set. [Heroku has some documentation on how to do so](https://devcenter.heroku.com/articles/config-vars)




