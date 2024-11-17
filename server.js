const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const { MongoClient, ObjectId } = require('mongodb');


app.use(express.json()); // Middleware para procesar el cuerpo de la solicitud JSON
app.use(express.urlencoded({ extended: true })); // Middleware para procesar el cuerpo de la solicitud URL-encoded

// URL de conexión a tu base de datos MongoDB
const url = 'mongodb+srv://robe:pnet@rrd-pnet-2023-2024.iw6l6to.mongodb.net/rrd-pnet-2023-2024?retryWrites=true&w=majority';

// Nombre de la colección en tu base de datos MongoDB
const collectionName = 'reservas';

// Establecer la conexión a la base de datos
MongoClient.connect(url, { useUnifiedTopology: true })
    .then(client => {
        console.log('Conexión establecida con la base de datos');
        const db = client.db();
        const collection = db.collection(collectionName);

        app.post('/reserva', (req, res) => {
            const { id,nickname, numCarnet, telefono, email, sala, fecha, hora, numPers, comentario } = req.body;
            console.log(`Recibida una solicitud: ${req.method} ${req.url} - IP del cliente: ${req.ip} - Body ${id} ${nickname} ${numCarnet} ${telefono} ${email} ${sala} ${fecha} ${hora} ${numPers} ${comentario}`); // Imprime el contenido del body en la consola

            // Insertar la nueva reserva en la base de datos
            collection.insertOne({ id,nickname, numCarnet, telefono, email, sala, fecha, hora, numPers, comentario })
                .then(result => {
                    console.log('Reserva añadida correctamente a la base de datos');
                    res.status(200).send('La información se ha añadido correctamente');
                })
                .catch(error => {
                    console.error('Error al añadir reserva a la base de datos:', error);
                    res.status(500).send('Ha ocurrido un error al añadir la información');
                });
        });

        app.get('/reservas', (req, res) => {
            // Obtener todas las reservas de la base de datos
            collection.find().toArray()
                .then(reservas => {
                    console.log(`Recibida una solicitud: ${req.method} ${req.url} - IP del cliente: ${req.ip}`); // Imprime el contenido del body en la consola
                    res.status(200).json(reservas);
                })
                .catch(error => {
                    console.error('Error al obtener reservas de la base de datos:', error);
                    res.status(500).json( 'Ha ocurrido un error al obtener las reservas');
                });
        });

    
        app.put('/reservas/:_id', (req, res) => {
            const _id = req.params._id; // Get the _id from the URL parameters
            const { nickname, numCarnet, telefono, email, sala, fecha, hora, numPers, comentario } = req.body;
          
            // Connect to the MongoDB database
            MongoClient.connect(url, { useUnifiedTopology: true })
              .then(client => {
                const db = client.db();
                const collection = db.collection(collectionName);
          
                // Find the reservation by its _id and update it
                collection.updateOne({ _id: new ObjectId(_id) }, { $set: { nickname, numCarnet, telefono, email, sala, fecha, hora, numPers, comentario } })
                  .then(result => {
                    if (result.modifiedCount > 0) {
                      console.log(`Reservation with _id ${_id} updated successfully`);
                      res.status(200).json('Reservation updated successfully');
                    } else {
                      console.error(`No reservation found with _id ${_id}`);
                      res.status(404).json('Reservation not found');
                    }
                  })
                  .catch(error => {
                    console.error('Error updating reservation:', error);
                    res.status(500).json('Error updating reservation');
                  });
              })
              .catch(error => {
                console.error('Error connecting to MongoDB:', error);
                res.status(500).json('Error connecting to MongoDB');
              });
          });
        
        
        
        

        app.delete('/reservas/:_id', (req, res) => {
            const _id = req.params._id; // Obtener el _id de la reserva de los parámetros de la URL
            // Convertir el _id a un objeto ObjectId
            const objectId = new ObjectId(_id);
            
            // Eliminar la reserva de la base de datos por su _id
            collection.deleteOne({ _id: objectId })
                .then(result => {
                    if (result.deletedCount > 0) {
                        console.log(`Reserva cancelada con éxito con _id ${_id}`);
                        res.status(200).json('Reserva eliminada con éxito'); // Enviar un mensaje de éxito si se elimina la reserva
                    } else {
                        console.error(`No se encontró ninguna reserva con el _id ${_id}`);
                        res.status(404).json('No se encontró ninguna reserva'); // Enviar un mensaje de error si no se encuentra ninguna reserva
                    }
                })
                .catch(error => {
                    console.error('Error al eliminar la reserva de la base de datos:', error);
                    res.status(500).json('Ha ocurrido un error al eliminar la reserva'); // Enviar un mensaje de error si ocurre un error al eliminar la reserva
                });
        });

        app.get('/reservas/:_id', (req, res) => {
            const _id = req.params._id; // Obtener el _id de la reserva de los parámetros de la URL
            // Convertir el _id a un objeto ObjectId
            const objectId = new ObjectId(_id);
        
            // Obtener la reserva de la base de datos por su _id
            collection.findOne({ _id: objectId })
                .then(reserva => {
                    if (reserva) {
                        console.log(`Recibida una solicitud: ${req.method} ${req.url} - IP del cliente: ${req.ip}`);
                        res.status(200).json(reserva); // Enviar la reserva como respuesta si se encuentra
                    } else {
                        console.error(`No se encontró ninguna reserva con el _id ${_id}`);
                        res.status(404).json('No se encontró ninguna reserva con ese id'); // Enviar un mensaje de error si no se encuentra ninguna reserva
                    }
                })
                .catch(error => {
                    console.error('Error al obtener la reserva de la base de datos:', error);
                    res.status(500).json('Ha ocurrido un error al obtener la reserva' ); // Enviar un mensaje de error si ocurre un error al obtener la reserva
                });
        });
    })

    .catch(error => {
        console.error('Error al conectar con la base de datos:', error);
    });

app.listen(3000, () => {
    console.log('Servidor escuchando en el puerto 3000');
});
