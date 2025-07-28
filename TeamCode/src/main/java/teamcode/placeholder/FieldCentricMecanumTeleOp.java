package teamcode.placeholder;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class FieldCentricMecanumTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        if (opModeInInit()) {
            // Declare our motors
            // Make sure your ID's match your configuration
            // By setting these values to new Gamepad(), they will default to all
            // boolean values as false and all float values as 0
            Gamepad currentGamepad1 = new Gamepad();
            Gamepad currentGamepad2 = new Gamepad();

            Gamepad previousGamepad1 = new Gamepad();
            Gamepad previousGamepad2 = new Gamepad();

            // other initialization code goes here


            // Store the gamepad values from the previous loop iteration in
            // previousGamepad1/2 to be used in this loop iteration.
            // This is equivalent to doing this at the end of the previous
            // loop iteration, as it will run in the same order except for
            // the first/last iteration of the loop.
            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);

            // Store the gamepad values from this loop iteration in
            // currentGamepad1/2 to be used for the entirety of this loop iteration.
            // This prevents the gamepad values from changing between being
            // used and stored in previousGamepad1/2.
            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
            DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
            DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
            DcMotor backRight = hardwareMap.dcMotor.get("backRight");
//            DcMotor Leftdepo = hardwareMap.dcMotor.get("leftdepo");
//            DcMotor Rightdepo = hardwareMap.dcMotor.get("Rightdepo");
//            DcMotor Intake = hardwareMap.dcMotor.get("Intake");
            // Servo outtakeclawServo = hardwareMap.servo.get("outtakeclaw");
            Servo intakeClaw = hardwareMap.servo.get("intakeClaw");
            // Servo intakewrist = hardwareMap.servo.get("intakewrist");

            boolean intakeToggle = false;
            // Reverse the right side motors. This may be wrong for your setup.
            // If your robot moves backwards when commanded to go forwards,
            // reverse the left side instead.
            // See the note about this earlier on this page.
            frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
            backRight.setDirection(DcMotorSimple.Direction.REVERSE);

            // Retrieve the IMU from the hardware map
            IMU imu = hardwareMap.get(IMU.class, "imu");
            // Adjust the orientation parameters to match your robot
            IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
            // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
            imu.initialize(parameters);
        }
                waitForStart();

                    if (isStopRequested()) {
                        return;
                    }
                        while (opModeIsActive()) {
                            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
                            double x = gamepad1.left_stick_x;
                            double rx = gamepad1.right_stick_x;

                            // This button choice was made so that it is hard to hit on accident,
                            // it can be freely changed based on preference.
                            // The equivalent button is start on Xbox-style controllers.
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

                            frontLeftMotor.setPower(frontLeftPower);
                            backLeft.setPower(backLeftPower);
                            frontRight.setPower(frontRightPower);
                            backRight.setPower(backRightPower);


                            // Rising edge detector
                            if (gamepad1.circleWasPressed()) {
                                // This will set intakeToggle to true if it was previously false
                                // and intakeToggle to false if it was previously true,
                                // providing a toggling behavior.
                                intakeToggle = !intakeToggle;
                            }

// Using the toggle variable to control the robot.
                            if (intakeToggle) {
                                intakeClaw.setPosition(.85);
                            } else {
                                intakeClaw.setPosition(.34);
                            }

                        }
                    }
                }
            }