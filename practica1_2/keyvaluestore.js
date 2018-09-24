var AWS = require('aws-sdk');
AWS.config.loadFromPath('./config.json');

var db = new AWS.DynamoDB();

function keyvaluestore(table) {
  this.LRU = require("lru-cache");
  this.cache = this.LRU({ max: 500 });
  this.tableName = table;
};

/**
 * Initialize the tables
 *
 */
keyvaluestore.prototype.init = function(whendone) {

  var tableName = this.tableName;
  var self = this;

  db.describeTable({TableName : tableName}, function (err, data) {
    if (err) {
      var params = {
          AttributeDefinitions: [
            {
            AttributeName: "Keyword",
            AttributeType: "S"
            },
            {
            AttributeName: "inx",
            AttributeType: "N"
            }
          ],
          KeySchema: [
            {
            AttributeName: "Keyword",
            KeyType: "HASH"
            },
            {
            AttributeName: "inx",
            KeyType: "RANGE"
            }
          ],
          ProvisionedThroughput: {
           ReadCapacityUnits: 1,
           WriteCapacityUnits: 1
          },
          TableName: tableName
        };
      db.createTable(params, function(err, data) {
        if (err) console.log(err, err.stack); // an error occurred
        else     console.log(data);           // successful response
      });
    }
    else {
      console.log(data);
    }
  })
  whendone(); //Call Callback function.
};

/**
 * Get result(s) by key
 *
 * @param search
 *
 * Callback returns a list of objects with keys "inx" and "value"
 */

keyvaluestore.prototype.get = function(search, callback) {
  var self = this;

  if (self.cache.get(search))
        callback(null, self.cache.get(search));
  else {
        var searchValues = search.split(" ");
        var items = [];
        searchValues.forEach(function (value) {

          var params = {
            ExpressionAttributeValues: {
             ":v1": {
               S: value
              }
            },
            KeyConditionExpression: "Keyword = :v1",
            TableName: self.tableName
           };

          db.query(params, function (err, data) {
                if (err) {
                  console.log(err);
                }
                else {
                  data.Items.forEach(function (item) {
                    items.push({"inx": item.inx.N, "value": item.Value.S, "key": item.Keyword.S});
                  })
                  self.cache.set(search, items);
                  console.log(items);
                  callback(err, items)
                }
          })
        })
  }
};


module.exports = keyvaluestore;
