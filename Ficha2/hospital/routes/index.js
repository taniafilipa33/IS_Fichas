var express = require("express");
var router = express.Router();
var Paciente = require("../controllers/Paciente");
var Pedido = require("../controllers/Pedidos");
var Consulta = require("../controllers/Consulta");
const axios = require("axios");

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
  axios
    .get("http://localhost:3002/pedidos")
    .then((dad) => {
      Pedido.updateJSONHospital(dad.data);
      axios
        .get("http://localhost:3000/pedidos")
        .then((d) => {
          //atualizar bd conforme os dados possivelmente novos aqui
          Pedido.updatePeds(d.data)
            .then((s) => {
              res.render("pedidos", { pedidos: d.data });
            })
            .catch((e) => console.log(e));
        })
        .catch((e) => console.log(e));
    })
    .catch((e) => console.log(e));
});

router.get("/exames", function (req, res, next) {
  axios
    .get("http://localhost:3002/exames")
    .then((d) => {
      //atualizar bd conforme os dados possivelmente novos aqui
      res.render("exames", { exames: d.data });
    })
    .catch((e) => console.log(e));
});

router.get("/inserirPedido", function (req, res, next) {
  res.render("insertPedido", { dados: {}, p: "Inserir Pedido" });
});

router.post("/inserirPedido", function (req, res, next) {
  var pedidos = {};
  Paciente.getPaciente(req.body.idPaciente)
    .then((pac) => {
      if (pac == "") {
        console.log("Paciente inxistente");
        res.render("insertPedido", {
          p: "Paciente Inexistente",
          dados: req.body,
        });
      } else {
        Consulta.getConsulta(req.body.idConsulta)
          .then((r) => {
            if (r == "") {
              res.render("insertPedido", {
                p: "Consulta Inexistente",
                dados: req.body,
              });
            } else {
              (pedidos.relatorio = "null"),
                (pedidos.descExame = req.body.descExame),
                (pedidos.idPaciente = req.body.idPaciente);
              pedidos.codigoExame = req.body.codExame;
              var today = new Date();
              var dd = String(today.getDate()).padStart(2, "0");
              var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
              var yyyy = today.getFullYear();
              today = yyyy + "-" + mm + "-" + dd;
              pedidos.estado = "pendente " + today;
              var dataHora =
                req.body.ano +
                "-" +
                req.body.mes +
                "-" +
                req.body.dia +
                " " +
                req.body.hora +
                ":" +
                req.body.minuto +
                ":00";
              pedidos.data = dataHora;
              (pedidos.mensagem = "teste de mensagem"),
                (pedidos.idConsulta = JSON.parse(JSON.stringify(r))[0][
                  "idConsulta"
                ]);
              Pedido.insertPedido(pedidos, pedidos.idConsulta)
                .then((o) => {
                  Pedido.getLast()
                    .then((idPed) => {
                      console.log(pedidos);
                      pedidos.id = idPed;
                      axios
                        .post("http://localhost:3000/pedidos", {
                          id: idPed,
                          estado: pedidos.estado,
                          data: pedidos.data,
                          idConsulta: pedidos.idConsulta,
                          idPaciente: pedidos.idPaciente,
                          codigoExame: pedidos.codigoExame,
                          descExame: pedidos.descExame,
                        })
                        .then(function (response) {
                          res.redirect("/pedidos");
                          console.log(response);
                        })
                        .catch(function (error) {
                          console.log(error);
                        });
                    })
                    .catch((e) => {
                      console.log(e);
                    });
                })
                .catch((e) => {
                  console.log(e);
                });
            }
          })
          .catch((e) => {
            console.log(e);
          });
      }
    })
    .catch((e) => {
      console.log(e);
    });
});

module.exports = router;
