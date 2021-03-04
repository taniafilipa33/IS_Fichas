var express = require("express");
var router = express.Router();
var Pedido = require("../controllers/marcacao");

/* GET home page. */
router.get("/", function (req, res, next) {
  Pedido.getPedidos().then((result) => {
    console.log(result);
    res.render("index", result);
  });
  //console.log(JSON.stringify(result));
});

/* GET home page. */
router.get("/:id", function (req, res, next) {
  var ped = Pedido.getPedido(req.params.id);
  res.render("pedido", ped);
});

module.exports = router;
