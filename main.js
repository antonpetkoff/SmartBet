var Algorithm, exec, fs, tmp;

exec = require('child_process').exec;
fs = require('fs');
tmp = require('tmp');

Algorithm = (function() {

  function Algorithm() {
    this.javaInstance = "java";
    this.javaClassDirectory = "bin;jars/*";
    this.outputEncoding = 'UTF-8';
    this.binary = 'nodeExecProcess.Main';
  }

  Algorithm.prototype.log = function() {
    console.log.apply(console, arguments);
  };

  Algorithm.prototype.runAlgorithm = function(team1, team2, callback) {
    var _this = this;
    tmp.tmpName(function(err, output) {
      var command;
      if (err) {
        callback(err, null);
        return;
      }
      command = [_this.javaInstance];
      if(_this.javaClassDirectory){
        command.push("-cp");
        command.push(_this.javaClassDirectory);
      }

      command.push(_this.binary);
      command.push(team1);
      command.push(team2);
      command.push(output+".txt");
      
      command = command.join(' ');
      _this.log("node-algorithm: Running '" + command + "'");
      exec(command, function(err, stdout, stderr) {
        var outputFile;
        if (err) {
          callback(err, null);
          return;
        }
        outputFile = output + '.txt';
        fs.readFile(outputFile, function(err, data) {
          if (!err) {
            data = data.toString(_this.outputEncoding);
          }
          _this.log("node-algorithm: Deleting '" + outputFile + "'");
          fs.unlink(outputFile, function(err) {});
          callback(err, data);
        });
      });
    });
  };

  return Algorithm;

})();

var algo = new Algorithm();
module.exports = algo;
