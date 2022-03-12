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
    const multiplayer = myDb.collection("multiplayer");

    app.post("/register", function(req, res) {
        const user = {
            name: req.body.name,
            password: req.body.password,
            date: req.body.date,
            points: 0,
            gamesPlayedToday: 0,
            type: "normal"
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
            password: req.body.password,
            type: "normal"
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
        var newDate = { $set: {date: req.body.date, gamesPlayedToday: parseInt(req.body.gamesPlayedToday)}};
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
            gamesPlayedToday: 0,
            type: req.body.type
        }

        collection.findOne({name: req.body.name, password: req.body.password, type: req.body.type}, function(err, result) {
            if(result == null) {
                collection.insertOne(user, function(err, result) {
                    res.status(200).send(user);
                })
            } else {
                res.status(200).send(result);
            }
        })


    })

    app.post("/addMultiplayer", function(req, res) {
        const user = {
            name: req.body.name,
            enemy: "",
            status: "waiting",
            points: 0  
        }

        multiplayer.findOne({status: "waiting"}, function(err, result) {
            if(result == null) {
                multiplayer.insertOne(user, function(err, result) {
                    res.status(201).send();
                })
            } else {
                var update = { $set: {status: "playing", enemy: req.body.name}};
                multiplayer.updateOne(result, update, function(err, resultUpdate) {})
                user.status = "playing";
                user.enemy = result.name;
                multiplayer.insertOne(user, function(err, result) {
                    res.status(200).send();
                })                
            }
        })
    })

    app.post("/removeMultiplayer", function(req, res) {
        multiplayer.deleteOne({name: req.body.name}, function(err, result) {
            res.status(200).send();
        })
    })

    app.post("/checkStatus", function(req, res) {
        multiplayer.findOne({name: req.body.name}, function(err, result) {
            if(result == null) {
                res.status(400).send();
            } else {
                switch(result.status) {
                    case "waiting": res.status(201).send(); break;
                    case "playing": res.status(200).send(); break;
                    case "finished": {
                        multiplayer.findOne({name: result.enemy}, function(err, enemy) {
                            if(enemy == null) {
                                return res.status(204).send();
                            } else if (enemy.status == "finished") {
                                return res.status(204).send();
                            } else {
                                return res.status(202).send();
                            }
                        })
                    } break;
                    default: res.status(404).send(); break;
                }
                
            }
        })
    })

    app.post("/finishMultiplayer", function(req, res) {
        var update = { $set: {status: "finished", points: parseInt(req.body.points)}};
        multiplayer.updateOne({name: req.body.name}, update, function(err, resultUpdate) {
            res.status(200).send();
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

