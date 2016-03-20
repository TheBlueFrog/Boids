/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


This was ripped off the Oracle site and minimally modified to
make viewer to my stuff, lots of heavy 3D juju...

Basically, if you setup your model to work around 0 < x, y, z < 1000
you should be able to not mess with things a lot
 */


package com.mike;

import com.mike.boids.Boid;
import com.mike.boids.Boids;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 *
 * @author cmcastil
 */
public class Viewer3D extends Application {

    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform mBoidGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -2500;
    private static final double CAMERA_INITIAL_X_ANGLE = 10.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 1000.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;
    
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    private static Boids mBoids = null;


    //   private void buildScene() {
    //       root.getChildren().add(world);
    //   }
    private void buildCamera() {
        System.out.println("buildCamera()");
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }


    private void buildAxes() {
        System.out.println("buildAxes()");

        Box x = buildAxis(Color.RED, AXIS_LENGTH * 2, 1, 1);
        x.setTranslateX(AXIS_LENGTH);

        Box y = buildAxis(Color.GREEN, 1, AXIS_LENGTH * 2, 1);
        y.setTranslateY(AXIS_LENGTH);

        Box z = buildAxis(Color.BLUE, 1, 1, AXIS_LENGTH * 2);
        z.setTranslateZ(AXIS_LENGTH);

        axisGroup.getChildren().addAll(x, y, z);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }

    private Box buildAxis(Color c, double x, double y, double z) {
        final PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(c);
        m.setSpecularColor(c);

        Box a = new Box(x, y, z);
        a.setMaterial(m);

        return a;
    }

    private void handleMouse(Scene scene, final Node root) {
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
              @Override
              public void handle(ScrollEvent event) {
                  double z = camera.getTranslateZ();
//                  System.out.println(String.format("%9.2f %92f", event.getDeltaX(), event.getDeltaY()));
                  double newZ = z + event.getDeltaY();
                  camera.setTranslateZ(newZ);
              }
          });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY); 
                
                double modifier = 1.0;
                
                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                } 
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }     
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);  
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);  
                }
                else if (me.isSecondaryButtonDown()) {
//                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
//                    cameraXform.rz.setAngle(cameraXform.rz.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);

                    double x = camera.getTranslateX();
                    double newx = x + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateX(newx);

                    double y = camera.getTranslateY();
                    double newy = x + mouseDeltaY*MOUSE_SPEED*modifier;
                    camera.setTranslateY(newy);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  
                }
            }
        });
    }
    
    private void handleKeyboard(Scene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z:
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                    case X:
                        axisGroup.setVisible(!axisGroup.isVisible());
                        break;
                    case V:
                        mBoidGroup.setVisible(!mBoidGroup.isVisible());
                        break;
                }
            }
        });
    }
    
    private void buildBoids() {

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);

        Xform boidForm = new Xform();

        List<Boid> v = mBoids.getBoids();
        for (Boid b : v) {
            Xform bForm = new Xform();

            Sphere bSphere = new Sphere(3.0);
            bSphere.setMaterial(redMaterial);

            bForm.setTranslateX(b.getLocation()[0]);
            bForm.setTranslateY(b.getLocation()[1]);
            bForm.setTranslateZ(b.getLocation()[2]);

            boidForm.getChildren().add(bForm);
            bForm.getChildren().add(bSphere);
        }

        mBoidGroup.getChildren().add(boidForm);

        world.getChildren().addAll(mBoidGroup);
    }

    @Override
    public void start(Stage primaryStage) {
        
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        mBoids = new Boids(50);

        buildCamera();
        buildAxes();
        buildBoids();

        Scene scene = new Scene(root, 1000, 1000, true);
        scene.setFill(Color.WHEAT);
        handleKeyboard(scene, world);
        handleMouse(scene, world);

//        primaryStage.setTitle("Molecule Sample Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setCamera(camera);

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (mBoids.move()) {

                    if ((++frameCounter % 30) == 0) {
                        double[] com = mBoids.getCenterOfMass();
                        addCenterOfMass(com);
                    }

                    List<Boid> v = mBoids.getBoids();

                    for (int i = 0; i < v.size(); ++i) {
                        Boid b = v.get(i);
                        double[] loc = b.getLocation();
                        Xform f = (Xform) mBoidGroup.getChildren().get(0);
                        Xform ff = (Xform) f.getChildren().get(i);
                        ff.setTranslateX(loc[0]);
                        ff.setTranslateY(loc[1]);
                        ff.setTranslateZ(loc[2]);
                    }
                }
            }
        }.start();
    }

    private long frameCounter = 0;

    private PhongMaterial mComMaterial = null;
    private void addCenterOfMass(double[] com) {
        if (mComMaterial == null) {
            mComMaterial = new PhongMaterial();
            mComMaterial.setDiffuseColor(Color.BLUE);
            mComMaterial.setSpecularColor(Color.LIGHTBLUE);
        }

        Xform f = new Xform();
        f.setTranslateX(com[0]);
        f.setTranslateY(com[1]);
        f.setTranslateZ(com[2]);

        mBoidGroup.getChildren().add(f);

        // only keep 10 around
        if (mBoidGroup.getChildren().size() > 20)
            mBoidGroup.getChildren().remove(1);

        Sphere s = new Sphere(5.0);
        s.setMaterial(mComMaterial);
        f.getChildren().add(s);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
