const csv = require('csv-parser');
const mongoClient = require("mongodb").MongoClient;
const url = "mongodb://localhost:27017/";
const fs = require('fs');

var easyBiology = {
    counter : 0,
    fullQuestion: {},
    distractors: [],
    allQuestions: []

};

var hardBiology = {
    counter : 0,
    fullQuestion: {},
    distractors: [],
    allQuestions: []

};

mongoClient.connect(url, function (err, db) {
    if (err) throw err;

    const myDb = db.db("testdb");
    const easy = myDb.collection("questions");
    const hard = myDb.collection("hard");

    fs.createReadStream('biologyEasy.csv')
        .pipe(csv())
        .on('data', (row) => { parseQuestions(row, easyBiology) })
        .on('end', () => {
            easyBiology.allQuestions.push(easyBiology.fullQuestion);

            easy.insertMany(easyBiology.allQuestions, function (err, res) {
                if (err) throw err;
                console.log('CSV file successfully processed');
            })
        });

    fs.createReadStream('biologyHard.csv')
        .pipe(csv())
        .on('data', (row) => { parseQuestions(row, hardBiology) })
        .on('end', () => {
            hardBiology.allQuestions.push(hardBiology.fullQuestion);

            hard.insertMany(hardBiology.allQuestions, function (err, res) {
                if (err) throw err;
                console.log('CSV file successfully processed');
                db.close();
            })
        });

})

function parseQuestions(row, obj) {
    if (obj.counter % 4 == 0) {
        if (obj.fullQuestion.question != null)
            obj.allQuestions.push(obj.fullQuestion);
        obj.fullQuestion = {};
        obj.distractors = [];
        obj.fullQuestion.question = row.question;
    }

    obj.distractors.push(row.distractor);
    obj.fullQuestion.distractors = obj.distractors;

    if (row.answer == 1)
        obj.fullQuestion.answer = row.distractor;

    obj.counter++;
}