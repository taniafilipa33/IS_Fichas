var express = require("express");
var router = express.Router();
var axios = require("axios");

/* GET home page. */
router.get("/", function (req, res, next) {
  res.render("index");
});

router.get("/pedidos", function (req, res, next) {
  axios
    .get("http://localhost:3000/pedidos")
    .then((dados) => {
      console.log(dados.data);
      var guarda = [];
      var today = new Date();
      var dd = String(today.getDate()).padStart(2, "0");
      var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
      var yyyy = today.getFullYear();
      today = yyyy + "-" + mm + "-" + dd;
      dados.data.forEach((element) => {
        if (element.data == today) guarda.push(element);
      });
      res.render("pedidos", { pedidos: guarda });
    })
    .catch((e) => {
      console.log(e);
    });
});

router.get("/aceitar/:id", function (req, res, next) {
  axios
    .get("http://localhost:3000/pedidos/" + req.params.id)
    .then((dados) => {
      var pedido = dados.data;
      var today = new Date();
      var dd = String(today.getDate()).padStart(2, "0");
      var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
      var yyyy = today.getFullYear();
      today = yyyy + "-" + mm + "-" + dd;
      pedido.estado = "aceite " + today;
      console.log(pedido);
      axios
        .put("http://localhost:3000/pedidos/" + req.params.id, {
          id: pedido.id,
          estado: pedido.estado,
          data: pedido.data,
          idConsulta: pedido.idConsulta,
          codigoExame: pedido.codigoExame,
          descExame: pedido.descExame,
        })
        .then((resp) => {
          axios
            .get("http://localhost:3000/pedidos")
            .then((dados) => {
              console.log(dados.data);
              var guarda = [];
              today = yyyy + "-" + mm + "-" + dd;
              dados.data.forEach((element) => {
                if (element.data == today) guarda.push(element);
              });
              res.render("pedidos", { pedidos: guarda });
            })
            .catch((e) => {
              console.log(e);
            });
        })
        .catch((e) => {
          console.log(e);
        });
    })
    .catch((e) => {
      console.log(e);
    });
});

module.exports = router;
