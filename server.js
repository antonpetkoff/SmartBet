var express = require("express"),
    app = express(),
    fs = require("fs"),
    data = {teams:["Manchester United", "Liverpool"]};
app.set("views", "./views");
app.set('view engine', 'jade');

app.use(express.static("public"));

app.get("/", function(req, res, next){
	if (fs.existsSync(__dirname+"/views/index.jade")) {
		res.render("index", {shop:data});
	}else {
    next();
  };
});

/*app.get("/product/:id", function(req, res, next){
  if (fs.existsSync(__dirname+"/views/index.jade")) {
    res.render("product", {shop:data.products[req.params.id]});
  }else {
    next();
  };
});*/

app.get("/:fileName", function(req, res, next){
	if(req.params && req.params.fileName && req.params.fileName != "product" && fs.existsSync(__dirname+"/views/"+req.params.fileName+".jade")){
		res.render(req.params.fileName, {shop:data});
	} else {
		next();
	}
})

var server = app.listen(3000, function () {

  var host = server.address().address
  var port = server.address().port

  console.log('Example app listening at http://%s:%s', host, port)

})