var express = require("express");
var router = express.Router();
var axios = require("axios");
var Pedido = require("../controllers/pedido");
var Exame = require("../controllers/exame");

/* GET home page. */
router.get("/", function (req, res, next) {
  res.render("index");
});

router.get("/pedidos", function (req, res, next) {
  axios
    .get("http://localhost:3000/pedidos")
    .then((dados) => {
      Pedido.updateJsonClinica(dados.data); //atualiza o json - server da clinica
      res.render("pedidos", { pedidos: dados.data });
    })
    .catch((e) => {
      console.log(e);
    });
});

router.get("/exames", function (req, res, next) {
  axios
    .get("http://localhost:3002/exames")
    .then(async (d) => {
      var array = []
        const pedidos = d.data.map(async i =>{
          Pedido.getPedido(i.Pedido_idPedido).then( p=>
            {
              array.push(p.relatorio)
            }
          )
        })
        await Promise.all(pedidos)
        res.render("exames", { exames: d.data, relatorios: array });
    })
    .catch((e) => console.log(e));
});

router.get("/realizar/:id", function (req, res, next) {
    res.render("relatorio", {id: req.params.id});
});

router.post("/realizar/:id", function (req, res, next) {
  Exame.getExame(req.params.id)
    .then((ex) =>{
    Pedido.insertRelatorio(req.body.relatorio, ex.Pedido_idPedido)
    .then((dados) =>{
      Pedido.getPedido(ex.Pedido_idPedido)
      .then((pedido) =>{
        axios
          .put("http://localhost:3002/pedidos/"+ ex.Pedido_idPedido,
          {
            id: pedido.id,
            estado: pedido.estado,
            data: pedido.data,
            relatorio: req.body.relatorio,
            idConsulta: pedido.idConsulta,
            codigoExame: pedido.codigoExame,
            descExame: pedido.descExame, 
          }).then((resp) => {
            res.redirect("/exames");
            }).catch((e) => console.log(e));
    }).catch((e) => console.log(e));
  }).catch((e) => console.log(e));
  }).catch((e) => console.log(e));
 
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
          relatorio: null,
        })
        .then((resp) => {
          var exame = {
            codigo_ato: pedido.codigoExame,
            ato: pedido.descExame,
            medico: "Alberto João Henriques",
            Paciente_idPaciente: 1,
            Pedido_idPedido: pedido.id,
          }
          Exame.insertExame(exame)
          .then((dados) => {
            Exame.getLast()
            .then((d) => {
            axios
              .post("http://localhost:3002/exames/",{
                id: d,
                codigo_ato: exame.codigo_ato,
                ato: pedido.descExame,
                medico: "Alberto João Henriques",
                Paciente_idPaciente: 1,
                Pedido_idPedido: pedido.id,
                data: pedido.data,
              })
            res.redirect("/pedidos");
          }).catch((e) => {
          console.log(e);
          })
        }).catch((e) => {
          console.log(e);
          })
        })
        .catch((e) => {
          console.log(e);
        });
    })
    .catch((e) => {
      console.log(e);
    });
});

router.get("/delete/:id", function (req, res, next) {
  axios
    .get("http://localhost:3000/pedidos/" + req.params.id)
    .then((dados) => {
      var pedido = dados.data;
      var today = new Date();
      var dd = String(today.getDate()).padStart(2, "0");
      var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
      var yyyy = today.getFullYear();
      today = yyyy + "-" + mm + "-" + dd;
      pedido.estado = "cancelado " + today;
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
          res.redirect("/pedidos");
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
