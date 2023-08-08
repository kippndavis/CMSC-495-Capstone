# API ('server-side") App

## Run Locally (for testing purposes)

- Ensure that the `Database/game/src/resources/application.properties` file contains the following:
    - `mongodb.connection.string=${MONGODB_CONNECTION_STRING}`
    - `mongodb.database.name=TriviaGPT`
    - `#server.port=${PORT}` *This should be commented for local build/run*
- `cd Database/game`
- `export MONGODB_CONNECTION_STRING="mongodb+srv://javaUser:<PASSWORD>@game-cluster.u7kyoxg.mongodb.net/?retryWrites=true&w=majority"` 
- `./gradlew clean build`
- `./gradlew run`

The app is configured to run on port **8080**, so you should now be able to test requests to **http://localhost:8080**.
The current APIs exposed are:
- `/questions/random/{category}`    *returns 25 random questions in specified category*
- `/questions/random/{category}/{numberOfQuestions}`    *returns number of questions requested in specified category*

Use cURL from a terminal to test the APIs
```shell
curl -X GET https://mkress-d8cb8becfffc.herokuapp.com/questions/random/Sports/10
[["Which country has won the most World Cup titles in cricket?","Australia","India","West Indies","England","0"],["Which country has won the most Rugby Six Nations Championships?","England","France","Ireland","Wales","3"],["Who holds the record for the most goals scored in a single season in the English Premier League?","Cristiano Ronaldo","Lionel Messi","Thierry Henry","Alan Shearer","3"],["Which team has won the most NBA MVP awards?","Los Angeles Lakers","Boston Celtics","Chicago Bulls","Houston Rockets","1"],["Who is the only athlete to have won Olympic gold medals in both the 100-meter and 200-meter sprints?","Usain Bolt","Carl Lewis","Jesse Owens","Michael Johnson","0"],["Who is the fastest woman in the world?","Florence Griffith-Joyner","Shelly-Ann Fraser-Pryce","Carmelita Jeter","Elaine Thompson-Herah","3"],["Who holds the record for the most home runs in a single MLB season?","Babe Ruth","Barry Bonds","Mark McGwire","Sammy Sosa","1"],["Which country has won the most Rugby World Cup titles?","New Zealand","Australia","South Africa","England","0"],["Which team has won the most UEFA European Championship titles?","Germany","Spain","Italy","France","1"],["Who is the only driver to have won the Indianapolis 500, the Monaco Grand Prix, and the 24 Hours of Le Mans?","Ayrton Senna","Mario Andretti","Fernando Alonso","Lewis Hamilton","2"]]
```

## Deploy and Run in Heroku

There are various ways to deploy an app to Heroku which is a PaaS cloud tool. The approach we have used is by connecting it as a git repo.
I personally chose to move the source code to another local folder in order to avoid conflict with GitHub remote and Heroku remote git repos. However, this is completely user preference.
### Requirments
- A Heroku Account
- A Heroku application
    - `heroku apps:create mkress`
### Move Source Code
- `cp -r Database/game ~/testbed/`
- `cd ~/testbed`
### Build/Re-build Gradle Project
- Un-comment the PORT variable in `Database/game/src/resources/application.properties`
    - `server.port=${PORT}` *This is a ENV variable specific to heroku*
    - Heroku uses random ports to listen on for API related apps and so the app needs to know what that port is so that it can bind to it
- `./gradlew clean build`
### Login to Heroku
- `heroku login`
    - Follow prompts and browser login steps
### Set Heroku ENV values
- `heroku config:set -a mkress MONGODB_CONNECTION_STRING="mongodb+srv://javaUser:<PASSWORD>@game-cluster.u7kyoxg.mongodb.net/?retryWrites=true&w=majority"`
- `heroku config:set -a mkress OPENAI_API_KEY=<API-KEY>`
### Set-up Heroku Git Repo
- `heroku git:remote -a mkress`
- `git add .` or `git add --all`
- `git add build/`
- `git commit -m "initial commit 4 heroku"`
- `git push heroku master`
- The app will build and you can follow the logs
### Watch logs for your app
- `heroku logs -a mkress -t`
### Restart App as Needed
- `heroku restart -a mkress`
### Get Endpoint Info
- `heroku info -a mkress`
- Web URL is what can be used for API calls
### Test/Sample API Calls
Ex Get 25 Science-Tech questions from the DB
- `curl -X GET https://mkress-d8cb8becfffc.herokuapp.com/questions/random/Science-Tech`

Ex. Get 10 History questions from the DB
- `curl -X GET https://mkress-d8cb8becfffc.herokuapp.com/questions/random/History/10`

Ex. Get 25 random Sports questions from OpenAI 
- `curl -X POST https://mkress-d8cb8becfffc.herokuapp.com/questions/gpt?category=Sports`

Ex. Get ranking by a given category 
- `curl -X GET https://mkress-d8cb8becfffc.herokuapp.com/scores/high-scores/geography`

Ex. Update a user's score
- `curl -X POST https://mkress-d8cb8becfffc.herokuapp.com/scores/submit-score/{category}`
  
  e.g. `"curl -X GET https://mkress-d8cb8becfffc.herokuapp.com/scores/submit-score/sports?initials=MJK&score=20"`
