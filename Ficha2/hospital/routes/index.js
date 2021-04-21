var express = require("express");
var router = express.Router();
var Paciente = require("../controllers/Paciente");
var Pedido = require("../controllers/Pedidos");
/* GET home page. */
router.get("/", function (req, res, next) {
  res.render("index");
});

router.get("/pacientes", function (req, res, next) {
  Paciente.getPacientes()
    .then((d) => {
      res.render("index");
    })
    .catch((e) => console.log(e));
});

router.get("/pedidos", function (req, res, next) {
  Pedido.getPedidos()
    .then((d) => {
      res.render("pedidos", { pedidos: d });
    })
    .catch((e) => console.log(e));
});

router.get("/inserirPedido", function (req, res, next) {
  res.render("insertPedido", { dados: {}, p: "Inserir Pedido" });
});

router.post("/inserirPedido", function (req, res, next) {
  console.log(req.body);
  var pedido = {};
  Paciente.getPaciente(req.body.idPaciente).then((pac) => {
    console.log(pac);
    if (pac == "[]")
      res.render("/inserirPedido", {
        p: "Paciente Inexistente",
        dados: req.body,
      });
    else {
      pedido.idPaciente = req.body.idPaciente;
    }
  });
  res.render("insertPedido");
});

module.exports = router;
