package teamcode.placeholder;


import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
@TeleOp 
public class Teleop extends LinearOpMode {
    //Declare vars
    public  DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public Servo clawServo;
    public IMU imu;
    public DcMotor intakeMotor;
    public DcMotor outtakeMotortop;
    public DcMotor outtakeMotorbottom;
    //change this var based on whether u want the claw open or closed when u start the program
    public boolean intakeToggle = false;

    @Override()
    public void runOpMode() {
        if (opModeInInit()) {
            frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
            frontRight = hardwareMap.get(DcMotor.class, "frontRight");
            backLeft = hardwareMap.get(DcMotor.class, "backLeft");
            backRight = hardwareMap.get(DcMotor.class, "backRight");
            clawServo = hardwareMap.get(Servo.class, "intakeClaw");
            intakeMotor = hardwareMap.get(DcMotor.class, "Horz");
            outtakeMotortop = hardwareMap.get(DcMotor.class, "outtakeTop");
            outtakeMotorbottom = hardwareMap.get(DcMotor.class, "outtakebottom");
             imu = hardwareMap.get(IMU.class, "imu");
            // Adjust the orientation parameters to match your robot
            IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
            // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
            imu.initialize(parameters);
        }
        waitForStart();
        while (opModeIsActive()) {
           double y = gamepad1.left_stick_y;
           double x = gamepad1.left_stick_x;
           double rx = gamepad1.right_stick_x;

                       if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);


            if (gamepad1.circleWasPressed()) {
                intakeToggle = !intakeToggle;
            }
            if (intakeToggle) {
                clawServo.setPosition(.85);
            } else {
                clawServo.setPosition(.34);
            }
        }
    }

}
