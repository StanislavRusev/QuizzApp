const express = require("express");
const app = express();
const mongoClient = require("mongodb").MongoClient;
const url = "mongodb://localhost:27017/";

app.use(express.json());

mongoClient.connect(url, function(err, db) {
    if(err) throw err;

    const myDb = db.db("testdb");
    const collection = myDb.collection("users");
    const easyBiologyCollection = myDb.collection("questions");
    const hardBiologyCollection = myDb.collection("hard");

    app.post("/register", function(req, res) {
        const user = {
            name: req.body.name,
            password: req.body.password,
            date: req.body.date,
            points: 0,
            gamesPlayedToday: 0
        }
        
        collection.findOne({name: req.body.name}, function(err, result) {
            if(result == null) {
                collection.insertOne(user, function(err, result) {
                    res.status(200).send();
                })
            } else {
                res.status(400).send();
            }
        })
    })

    app.post("/login", function(req, res) {
        const user = {
            name: req.body.name,
            password: req.body.password
        }

        collection.findOne(user, function(err, result) {
            if(result != null) {
                const userResult = {
                    name: result.name,
                    points: result.points,
                    date: result.date,
                    gamesPlayedToday: result.gamesPlayedToday
                }
                res.status(200).send(JSON.stringify(userResult));
            } else {
                res.status(404).send();
            }
        })
    })

    app.get("/easyBiology", function(req, res) {
        easyBiologyCollection.find({}, {projection: {_id: 0, question: 1, distractors: 1, answer: 1 }}).toArray(function(err, result) {
            var resultQuestions = getQuestions(result.length, result, 10);
            res.status(200).send(JSON.stringify(resultQuestions));
        })
    })

    app.get("/hardBiology", function(req, res) {
        hardBiologyCollection.find({}, {projection: {_id: 0, question: 1, distractors: 1, answer: 1 }}).toArray(function(err, result) {
            var resultQuestions = getQuestions(result.length, result, 5);
            res.status(200).send(JSON.stringify(resultQuestions));
        })
    })

    app.post("/update", function(req, res) {
        var newPoints = { $set: { points: parseInt(req.body.points) }};
        collection.updateOne({ name: req.body.name }, newPoints, function(err, result) {
            res.status(200).send();
        })
    })

    app.get("/users", function(req, res) {
        collection.find({}, {projection: {_id:0, name: 1, points: 1}}).sort({points : -1}).toArray(function(err, result) {
            res.status(200).send(JSON.stringify(result));
        }) 
    })

    app.post("/updateLastPlayed", function(req, res) {
        var newDate = { $set: {date: req.body.date, gamesPlayedToday: parseInt(req.body.gamesPlayedToday)}}
        collection.updateOne({ name: req.body.name }, newDate, function(err, result) {
            res.status(200).send();
        })
    })

    app.post("/socialMedia", function(req, res) {
        const user = {
            name: req.body.name,
            password: req.body.password,
            date: req.body.date,
            points: 0,
            gamesPlayedToday: 0
        }

        collection.findOne({name: req.body.name, password: req.body.password}, function(err, result) {
            if(result == null) {
                collection.insertOne(user, function(err, result) {
                    res.status(200).send(user);
                })
            } else {
                res.status(200).send(result);
            }
        })


    })
})

app.listen(8080, () => {
    console.log("Listening on 8080...");
})

function randomNumberGenerate(max) {
    return Math.floor(Math.random() * (max));
}

function getQuestions(max, questions, totalCount) {
    var indexes = new Set();
    var result = new Array();

    while(indexes.size != totalCount) {
        indexes.add(randomNumberGenerate(max));
    }

    for(i of indexes) {
        result.push(questions[i]);
    }

    return result;
}

