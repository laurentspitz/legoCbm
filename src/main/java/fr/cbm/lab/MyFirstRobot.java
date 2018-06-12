package fr.cbm.lab;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;
import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import ev3dev.sensors.Battery;
import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import org.zeroturnaround.zip.ZipUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

public class MyFirstRobot {

    //Robot Configuration
    //private static final EV3TouchSensor touch1 = new EV3TouchSensor(SensorPort.S3);
    private static final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.C);

    private static final int IMG_NUMBER = 24; // Nombre d'image que l'on veut prendre en photo
    private static final String IMG_FORMAT = "jpg";
    private static final WebcamResolution IMG_RES = WebcamResolution.HD720;

    private static final int DEGREE_TO_TURN = 360 / IMG_NUMBER;


    // set capture driver for fswebcam tool
    static {
        Webcam.setDriver(new FsWebcamDriver());
    }

    public static void main(String[] args) {

        //String imgFolder = String.valueOf(System.currentTimeMillis());
        String imgFolder = "34PC94";

        File directory = new File(imgFolder);
        if (! directory.exists()){
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        System.out.println("Checking Battery Voltage: " + Battery.getInstance().getVoltage());

        //To Stop the motor in case of pkill java for example
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Emergency Stop");
                motor.stop();
            }
        }));

        //Init motor
        motor.brake();
        motor.setSpeed(200);
        motor.resetTachoCount();

        //Init camera
        Webcam webcam = Webcam.getDefault();
        webcam.setCustomViewSizes(new Dimension[] { IMG_RES.getSize() }); // register custom size
        webcam.setViewSize(IMG_RES.getSize()); // set size

        // fait tourner le plateau + capture l 'image
        System.out.println("Go Forward with the motors. Start Position : " +  motor.getTachoCount());

        try {
            webcam.open();

            //3 photos pour la mise au point (les 3 1er images peuvent etre sombre le temps de la mise au point)
            webcam.getImage();
            webcam.getImage();
            webcam.getImage();
            webcam.getImage();
            webcam.getImage();

            for (int i = 1; i < IMG_NUMBER + 1; i++) {
                //motor.rotateTo(motor.getTachoCount() + DEGREE_TO_TURN, true);
                motor.rotate(DEGREE_TO_TURN, true);
                System.out.println(String.format("Large Motor is moving: %s to position %d at speed %d", motor.isMoving(), motor.getTachoCount(), motor.getSpeed()));
                Delay.msDelay(20);

                ImageIO.write(webcam.getImage(), IMG_FORMAT, new File(imgFolder + "/"+ i + "." + IMG_FORMAT));
                sendFile(imgFolder + "/"+ i + "." + IMG_FORMAT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webcam.close();
        }

        //Delay.msDelay(5000);
        System.out.println("Stopped motors");

        // On arrete le moteur
        motor.stop();
        System.out.println("End motor Position : " +  motor.getTachoCount());
        System.out.println("Battery Voltage: " + Battery.getInstance().getVoltage());


     //   ZipUtil.pack(new File("/home/robot/" + imgFolder + "/"), new File("/home/robot/"+ imgFolder +".zip"));

//        sendFile(imgFolder +".zip");

        System.exit(0);

    }

    public static void sendFile(String filetoSendName) {
        System.out.println("filetoSendName: " + filetoSendName);
        String bucketName = "lab-trois-six-zero";
        String uploadFileName = "pictures/";

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

            uploadFileName = uploadFileName + filetoSendName;
            File file = new File("/home/robot/" + filetoSendName);
            PutObjectResult result = s3Client.putObject(bucketName, uploadFileName, file);
            System.out.println("result : " + result.getContentMd5());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
}
