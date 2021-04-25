var express = require("express");
var router = express.Router();
var axios = require("axios");
var Pedido = require("../controllers/pedido");
var Exame = require("../controllers/exame");
const Vonage = require("@vonage/server-sdk");
var Paciente = require("../controllers/paciente");

const vonage = new Vonage({
  apiKey: "06b932d7",
  apiSecret: "d6cwUZDsyWBTZSlS",
});

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
      var s = [];
      const pedidos = d.data.map(async (i) => {
        await Pedido.getPedido(i.Pedido_idPedido)

          .then((p) => {
            var ped = {};
            ped.id = i.id;
            ped.codigo_ato = i.codigo_ato;
            ped.ato = i.ato;
            ped.medico = i.medico;
            ped.data = i.data;
            ped.Paciente_idPaciente = i.Paciente_idPaciente;
            ped.Pedido_idPedido = i.Pedido_idPedido;
            ped.relatorio = p.relatorio;
            s.push(ped);
          })
          .catch((e) => console.log(e));
      });
      await Promise.all(pedidos);
      res.render("exames", { exames: s });
    })
    .catch((e) => console.log(e));
});

router.get("/realizar/:id", function (req, res, next) {
  res.render("relatorio", { id: req.params.id });
});

const from = "Vonage APIs";
const to = "351934579095";
const text = "O seu Relatorio de exame esta disponivel";

router.post("/realizar/:id", function (req, res, next) {
  Exame.getExame(req.params.id)
    .then((ex) => {
      Pedido.insertRelatorio(req.body.relatorio, ex.Pedido_idPedido)
        .then((dados) => {
          Pedido.getPedido(ex.Pedido_idPedido)
            .then((pedido) => {
              console.log(pedido);
              Paciente.getPaciente(ex.Paciente_idPaciente)
                .then((p) => {
                  vonage.message.sendSms(
                    from,
                    p.telefone,
                    text,
                    (err, responseData) => {
                      if (err) {
                        console.log(err);
                      } else {
                        if (responseData.messages[0]["status"] === "0") {
                          console.log("Message sent successfully.");
                        } else {
                          console.log(
                            `Message failed with error: ${responseData.messages[0]["error-text"]}`
                          );
                        }
                      }
                    }
                  );
                  axios
                    .put(
                      "http://localhost:3002/pedidos/" + ex.Pedido_idPedido,
                      {
                        id: pedido.idPedido,
                        estado: pedido.estado,
                        data: pedido.data,
                        relatorio: req.body.relatorio,
                        idConsulta: pedido.idConsulta,
                        codigoExame: pedido.codigoExame,
                        descExame: pedido.descExame,
                      }
                    )
                    .then((resp) => {
                      res.redirect("/exames");
                    })
                    .catch((e) => console.log(e));
                })
                .catch((e) => console.log(e));
            })
            .catch((e) => console.log(e));
        })
        .catch((e) => console.log(e));
    })
    .catch((e) => console.log(e));
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
          idPaciente: pedido.idPaciente,
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
          };
          Exame.insertExame(exame)
            .then((dados) => {
              Exame.getLast()
                .then((d) => {
                  axios.post("http://localhost:3002/exames/", {
                    id: d,
                    codigo_ato: exame.codigo_ato,
                    ato: pedido.descExame,
                    medico: "Alberto João Henriques",
                    Paciente_idPaciente: 1,
                    Pedido_idPedido: pedido.id,
                    data: pedido.data,
                  });
                  res.redirect("/pedidos");
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
          idPaciente: pedido.idPaciente,
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
