const AWS = require('aws-sdk');
const rekognition = new AWS.Rekognition();

exports.handler = async (event) => {
    let fileName = event.fileName;
    let data64 = event.data64;

    try {
        const body = await getLabels(data64);
        const labels = body.Labels;
    
        var response = {
            fileName: fileName,
            Tags: labels
        };
    
        return response;
    } catch (err) {
        var response = {
            fileName: fileName,
            Error: {
                Error: err.message,
                Description: err.stack
            }
        };   
        return response;
    }
};

const getLabels = async(data64) => {
    var params = {
        Image: { 
            Bytes: new Buffer(data64, 'base64')
        },
        MaxLabels: 10,
        MinConfidence: 75.0
    };
  
    return rekognition.detectLabels(params, function(err, data) {
        if (err) {
            //console.log(err, err.stack);
            return err;
        } 
        else {
            //console.log(data); 
            return data;
        }
    }).promise();
}