# Set Up Atlas CLI
Download the latest atlas CLI. 
e.g. for MacOS

```brew install mongodb-atlas```

Create or Register an existing account.

```atlas auth register```

Follow the prompts

# Stand up the MongoDB Atlas Cluster

```atlas cluster -f game-cluster.json```

or

```atlas cluster create game-cluster --projectId 64a4c037d1c62007c7b3b6d7 --provider AWS --region US_EAST_1 --tier M0```

# Add connection IPs
Example:

```atlas accessList create 192.168.1.38 --type ipAddress --projectId 64a4c037d1c62007c7b3b6d7 --comment "IP for MikeK MacBook" --output json```

Expected return:

```{
  "links": [
    {
      "href": "https://cloud.mongodb.com/api/atlas/v2/groups/64a4c037d1c62007c7b3b6d7/accessList?includeCount=true\u0026pageNum=1\u0026itemsPerPage=100",
      "rel": "self"
    }
  ],
  "results": [
    {
      "cidrBlock": "192.168.1.38/32",
      "comment": "IP for MikeK MacBook",
      "groupId": "64a4c037d1c62007c7b3b6d7",
      "ipAddress": "192.168.1.38",
      "links": [
        {
          "href": "https://cloud.mongodb.com/api/atlas/v1.0/groups/64a4c037d1c62007c7b3b6d7/accessList/192.168.1.38%2F32",
          "rel": "self"
        }
      ]
    }
  ],
  "totalCount": 1
}
```

# Add DB user
### Admin User
```atlas dbusers create atlasAdmin --username admin --projectId 64a4c037d1c62007c7b3b6d7```

Enter a password when prompted.

### Java DBuser

```atlas dbusers create readWriteAnyDatabase --username javaUser --projectId 64a4c037d1c62007c7b3b6d7```
Enter a password when prompted.

# Connect to Cluster

Install `mongosh`

```homebrew install mongosh```

Connect to DB

```mongosh "mongodb+srv://game-cluster.u7kyoxg.mongodb.net/" --apiVersion 1 --username <username>```
```
Enter password: *************
Current Mongosh Log ID:	64a62849c3a9b7894a5eaee8
Connecting to:		mongodb+srv://<credentials>@game-cluster.u7kyoxg.mongodb.net/?appName=mongosh+1.10.1
Using MongoDB:		6.0.6 (API Version 1)
Using Mongosh:		1.10.1

For mongosh info see: https://docs.mongodb.com/mongodb-shell/

Warning: Found ~/.mongorc.js, but not ~/.mongoshrc.js. ~/.mongorc.js will not be loaded.
  You may want to copy or rename ~/.mongorc.js to ~/.mongoshrc.js.
Atlas atlas-omrt7t-shard-0 [primary] test> 
```

# Insert Data
### Initial Insert

```mongoimport --uri mongodb+srv://game-cluster.u7kyoxg.mongodb.net/ -u admin -p <PASSWORD> -d TriviaGPT -c trivia_questions --file mathematics.json```

`-u` username\
`-p` password\
`-d` Database name (TriviaGPT)\
`-c` Collection Name

### Update Insertions
When updating a document add the `--drop` argument. There are technically other methods using `--mode=upsert` with
subsequent `--upsertFields` arguments but especially on larger updates it would get very confusing. This will essentially
drop the collection and re-write it assuming your using the same file again with changes or updates. 

```mongoimport --uri mongodb+srv://game-cluster.u7kyoxg.mongodb.net/ -u admin -p <PASSWORD> -d TriviaGPT -c trivia_questions --drop --file mathematics.json```

### Update a Single Document in Collection
The way our data will be structured is that there will be collections with multiple documents. For example, in the 
trivia_questions collection there will be a document for each category of questions. Therefore, it may be likely that 
there will be a scenario where a single document in a collection of multiple documents will need to be updated. In this 
scenario it's not practical to use `mongoimport`, even though it is more useful for initial import functions. Otherwise, 
we should make use of the `mongosh` when logged in to the DB. 

First, load the JSON file for the document being replaced and define it:
```const newDocument = JSON.parse(cat("path/to/json"));```

Replace the document using `replaceOne()`:
```
db.collectionName.replaceOne(
  { fieldToMatch: "valueToMatch" },  // Match condition
  newDocument                        // Replacement document
)
```

Here is an example:
```
Atlas atlas-omrt7t-shard-0 [primary] test> db.mike_sample.find()
[
  {
    _id: ObjectId("64a76b1f0389ac33d95e5f7d"),
    module: {
      number: 1,
      name: 'Topic 1',
      questions: 1,
      revision: '2023-07-06'
    },
    questions: [
      {
        number: 0,
        question: 'sample 1: ',
        answers: [ 'A', 'b', 'C', 'D' ],
        correct_answer: 1
      }
    ]
  },
  {
    _id: ObjectId("64a76bbb0b1f79d448ba25e3"),
    module: {
      number: 1,
      name: 'Mathematics',
      questions: 2,
      revision: '2023-07-05'
    },
    questions: [
      {
        number: 1,
        question: 'Eighteen thousandths, written as a decimal, is: ',
        answers: [ '0.0018', '0.018', '0.18' ],
        correct_answer: 1
      },
      {
        number: 2,
        question: 'The next number in the sequence <b>1, 3, 6, 10, </b> is: ',
        answers: [ '12', '13', '14', '15' ],
        correct_answer: 4
      }
    ]
  }

Atlas atlas-omrt7t-shard-0 [primary] test> const newDoc = JSON.parse(cat("question_data/test-sample.json"));

Atlas atlas-omrt7t-shard-0 [primary] test> db.mike_sample.replaceOne({ "module.name": "Topic 1" }, newDoc );
{
  acknowledged: true,
  insertedId: null,
  matchedCount: 1,
  modifiedCount: 1,
  upsertedCount: 0
}

Atlas atlas-omrt7t-shard-0 [primary] test> db.mike_sample.find()
[
  {
    _id: ObjectId("64a76b1f0389ac33d95e5f7d"),
    module: {
      number: 1,
      name: 'Topic 1',
      questions: 1,
      revision: '2023-07-07'
    },
    questions: [
      {
        number: 1,
        question: 'question 1: ',
        answers: [ 'A', 'B', 'C', 'D' ],
        correct_answer: 3
      }
    ]
  },
  {
    _id: ObjectId("64a76bbb0b1f79d448ba25e3"),
    module: {
      number: 1,
      name: 'Mathematics',
      questions: 2,
      revision: '2023-07-05'
    },
    questions: [
      {
        number: 1,
        question: 'Eighteen thousandths, written as a decimal, is: ',
        answers: [ '0.0018', '0.018', '0.18' ],
        correct_answer: 1
      },
      {
        number: 2,
        question: 'The next number in the sequence <b>1, 3, 6, 10, </b> is: ',
        answers: [ '12', '13', '14', '15' ],
        correct_answer: 4
      }
    ]
  }
]

```

In the above example the revision date was changed as well as the correct answer and one of the questions. The data
in the document that did not match the mathing criteria remained the same. 