package com.sjq.gourd.client;

import com.sjq.gourd.bullet.Bullet;
import com.sjq.gourd.collision.Collision;
import com.sjq.gourd.constant.Constant;
import com.sjq.gourd.constant.CreatureId;
import com.sjq.gourd.constant.ImageUrl;
import com.sjq.gourd.creature.Creature;
import com.sjq.gourd.equipment.Equipment;
import com.sjq.gourd.equipment.EquipmentFactory;
import com.sjq.gourd.protocol.BulletBuildMsg;
import com.sjq.gourd.protocol.EquipmentRequestMsg;
import com.sjq.gourd.protocol.Msg;
import com.sjq.gourd.stage.SceneController;
import com.sjq.gourd.tool.PositionXY;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameStartFight {
    private SceneController sceneController = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String campType = "";
    private HashMap<Integer, Creature> myFamily = null;
    private HashMap<Integer, Creature> enemyFamily = null;

    private Creature myCreature = null;
    private Creature enemyCreature = null;
    private final ImageView myCreatureImageView = new ImageView();
    private final ImageView enemyCreatureImageView = new ImageView();
    private final Text myCreatureText = new Text();
    private final Text enemyCreatureText = new Text();

    private ConcurrentHashMap<Integer, Bullet> bullets = new ConcurrentHashMap<>();

    private EquipmentFactory equipmentFactory = null;
    private HashMap<Integer, Equipment> equipmentHashMap = new HashMap<>();

    private MsgController msgController;

    private boolean updateFlag = false;


    public GameStartFight(String campType, SceneController sceneController,
                          ObjectInputStream in, ObjectOutputStream out,
                          HashMap<Integer, Creature> myFamily, HashMap<Integer, Creature> enemyFamily,
                          EquipmentFactory equipmentFactory) {
        this.campType = campType;
        this.sceneController = sceneController;
        this.in = in;
        this.out = out;
        this.myFamily = myFamily;
        this.enemyFamily = enemyFamily;
        this.equipmentFactory = equipmentFactory;
        if (campType.equals(Constant.CampType.GOURD))
            msgController = new MsgController(myFamily, enemyFamily, equipmentFactory);
        else
            msgController = new MsgController(enemyFamily, myFamily, equipmentFactory);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sceneController.getFightScene().getChildren().add(myCreatureImageView);
                sceneController.getFightScene().getChildren().add(enemyCreatureImageView);
                sceneController.getFightScene().getChildren().add(myCreatureText);
                sceneController.getFightScene().getChildren().add(enemyCreatureText);
                myCreatureImageView.setVisible(false);
                myCreatureImageView.setDisable(true);
                myCreatureImageView.setPreserveRatio(true);
                myCreatureImageView.setFitWidth(80);
                myCreatureImageView.setLayoutY(20);
                enemyCreatureImageView.setVisible(false);
                enemyCreatureImageView.setDisable(true);
                enemyCreatureImageView.setPreserveRatio(true);
                enemyCreatureImageView.setFitWidth(80);
                myCreatureText.setVisible(false);
                enemyCreatureText.setVisible(false);

            }
        });
    }

//    public void initGame() {
//        int idOffset = CreatureId.MIN_GOURD_ID;
//        if (this.campType.equals(Constant.CampType.MONSTER))
//            idOffset = CreatureId.MIN_MONSTER_ID;
//        for (Creature creature : myFamily.values()) {
//            ImageView imageView = creature.getCreatureImageView();
//            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    if (enemyCreature != creature && myCreature != null
//                            && myCreature.isAlive() && myCreature.getCreatureId() == CreatureId.GRANDPA_ID) {
//                        enemyCreature = creature;
//                        myCreature.setPlayerAttackTarget(enemyCreature);
//                    }
//                    if (myCreature == null || !myCreature.isAlive()) {
//                        myCreature = creature;
//                        myCreature.flipControlled();
//                    } else if (myCreature != creature) {
//                        myCreature.flipControlled();
//                        myCreature = creature;
//                        myCreature.flipControlled();
//                    } else if (!myCreature.isControlled()) {
//                        myCreature.flipControlled();
//                    }
//                }
//            });
//        }
//        for (Creature creature : enemyFamily.values()) {
//            ImageView imageView = creature.getCreatureImageView();
//            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    if (enemyCreature != creature) {
//                        enemyCreature = creature;
//                        if (myCreature != null && myCreature.isAlive())
//                            myCreature.setPlayerAttackTarget(enemyCreature);
//                    }
//                }
//            });
//        }
//        sceneController.getMapPane().setFocusTraversable(true);
//
//        final boolean[] isUpPressOn = {false};
//        final boolean[] isDownPressOn = {false};
//        final boolean[] isLeftPressOn = {false};
//        final boolean[] isRightPressOn = {false};
//        final int[] lastPressOn = {Constant.Direction.STOP};
//        int finalIdOffset = idOffset;
//        sceneController.getMapPane().setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                KeyCode keyCode = event.getCode();
//                if (keyCode == KeyCode.W) {
//                    isUpPressOn[0] = true;
//                    lastPressOn[0] = Constant.Direction.UP;
//                } else if (keyCode == KeyCode.S) {
//                    isDownPressOn[0] = true;
//                    lastPressOn[0] = Constant.Direction.DOWN;
//                } else if (keyCode == KeyCode.A) {
//                    isLeftPressOn[0] = true;
//                    lastPressOn[0] = Constant.Direction.LEFT;
//                } else if (keyCode == KeyCode.D) {
//                    isRightPressOn[0] = true;
//                    lastPressOn[0] = Constant.Direction.RIGHT;
//                } else if (keyCode == KeyCode.Q) {
//                    if (myCreature != null && myCreature.isAlive()) {
//                        myCreature.setQFlag(true);
//                    }
//                } else if (keyCode == KeyCode.E) {
//                    if (myCreature != null && myCreature.isAlive())
//                        myCreature.setEFlag(true);
//                } else if (keyCode == KeyCode.R) {
//                    if (myCreature != null && myCreature.isAlive())
//                        myCreature.setRFlag(true);
//                }
//                if (myCreature != null) {
//                    if (isLeftPressOn[0] || isRightPressOn[0] || isUpPressOn[0] || isDownPressOn[0])
//                        myCreature.setDirection(lastPressOn[0]);
//                    else
//                        myCreature.setDirection(Constant.Direction.STOP);
//                }
//                if (keyCode.isDigitKey()) {
//                    int num = keyCode.ordinal() - 25;
//                    System.out.println(num);
//                    if (0 <= num && num <= 8) {
//                        Creature creature = myFamily.get(finalIdOffset + num);
//                        if (myCreature != creature) {
//                            if (creature != null && creature.isAlive()) {
//                                if (myCreature != null && myCreature.isAlive() && myCreature.isControlled())
//                                    myCreature.flipControlled();
//                                if (!creature.isControlled())
//                                    creature.flipControlled();
//                                myCreature = creature;
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//        sceneController.getMapPane().setOnKeyReleased(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                KeyCode keyCode = event.getCode();
//                if (keyCode == KeyCode.W)
//                    isUpPressOn[0] = false;
//                else if (keyCode == KeyCode.S)
//                    isDownPressOn[0] = false;
//                else if (keyCode == KeyCode.A)
//                    isLeftPressOn[0] = false;
//                else if (keyCode == KeyCode.D)
//                    isRightPressOn[0] = false;
//                if (myCreature != null) {
//                    if (isLeftPressOn[0] || isRightPressOn[0] || isUpPressOn[0] || isDownPressOn[0])
//                        myCreature.setDirection(lastPressOn[0]);
//                    else
//                        myCreature.setDirection(Constant.Direction.STOP);
//                }
//            }
//        });
//    }

    public void start() {
//        initGame();
        init(campType, myFamily, enemyFamily);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int msgType = in.readInt();
                        if (msgType == Msg.UPDATE_FLAG_MSG) {
                            updateFlag = true;
                        } else {
                            msgController.getMsgClass(msgType, in);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int bulletKey = 0;
                if (campType.equals(Constant.CampType.MONSTER))
                    bulletKey = 1;
                while (true) {
                    try {
                        for (Creature myMember : myFamily.values()) {
                            ArrayList<Bullet> tempBullet = myMember.update();
                            if (tempBullet.size() != 0) {
                                Iterator<Bullet> bulletIterator = tempBullet.listIterator();
                                while (bulletIterator.hasNext()) {
                                    Bullet bullet = bulletIterator.next();
                                    if(bullet.getBulletType() == Constant.REMOTE_BULLET_TYPE) {
                                        bullets.put(bulletKey, bullet);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                sceneController.getMapPane().getChildren().add(bullet.getCircleShape());
                                            }
                                        });
                                    }
                                    new BulletBuildMsg(bulletKey,
                                            bullet.getSourceCreature().getCampType(), bullet.getSourceCreature().getCreatureId(),
                                            bullet.getTargetCreature().getCampType(), bullet.getTargetCreature().getCreatureId(),
                                            bullet.getBulletType(), bullet.getBulletState().ordinal()).sendMsg(out);
                                    bulletKey += 2;
                                    if(bulletKey > Integer.MAX_VALUE - 50) {
                                        bulletKey = 0;
                                        if (campType.equals(Constant.CampType.MONSTER))
                                            bulletKey = 1;
                                    }
                                }
                            }
                        }

                        for (Creature creature : enemyFamily.values()) {
                            creature.notMyCampUpdate();
                        }

                        HashMap<Integer, Bullet> buildBullets = msgController.getBullets();
                        if (buildBullets.size() != 0) {
                            Iterator<Map.Entry<Integer, Bullet>> bulletMapIterator = buildBullets.entrySet().iterator();
                            while (bulletMapIterator.hasNext()) {
                                Map.Entry<Integer, Bullet> bulletEntry = bulletMapIterator.next();
                                int key = bulletEntry.getKey();
                                Bullet bullet = bulletEntry.getValue();
                                bullets.put(key, bullet);
                                if (bullet.getBulletType() == Constant.REMOTE_BULLET_TYPE) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            sceneController.getMapPane().getChildren().add(bullet.getCircleShape());
                                        }
                                    });
                                }
                            }
                        }

                        HashMap<Integer, PositionXY> moveBullets = msgController.getMoveBullets();
                        for(Map.Entry<Integer, PositionXY> entry : moveBullets.entrySet()) {
                            int key = entry.getKey();
                            PositionXY positionXY = entry.getValue();
                            if(bullets.get(key) != null)
                                bullets.get(key).setImagePosition(positionXY.X, positionXY.Y);
                        }

                        ArrayList<Integer> deleteBulletKeys = msgController.getDeleteBulletKeys();
                        for(int key : deleteBulletKeys) {
                            bullets.get(key).setValid(false);
                        }

                        HashMap<Integer, Equipment> buildEquipment = msgController.getBuildEquipment();
                        for(Map.Entry<Integer, Equipment> entry : buildEquipment.entrySet()) {
                            equipmentHashMap.put(entry.getKey(), entry.getValue());
                        }

                        Iterator<Bullet> bulletIterator = bullets.values().iterator();
                        while (bulletIterator.hasNext()) {
                            Bullet bullet = bulletIterator.next();
                            if (bullet.isValid()) {
                                bullet.draw();
                            } else {
                                new Collision(bullet).collisionEvent();
                                bulletIterator.remove();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        bullet.getCircleShape().setVisible(false);
                                        sceneController.getMapPane().getChildren().remove(bullet.getCircleShape());
                                    }
                                });
                            }
                        }

                        HashMap<Creature, Integer> equipmentPickUp = msgController.getEquipmentPickUp();
                        for(Map.Entry<Creature, Integer> entry : equipmentPickUp.entrySet()) {
                            Creature creature = entry.getKey();
                            int equipmentKey = entry.getValue();
                            if(equipmentHashMap.get(equipmentKey) != null) {
                                Equipment equipment = equipmentHashMap.get(equipmentKey);
                                creature.pickUpEquipment(equipment);
                                equipmentHashMap.remove(equipmentKey);
                            }
                        }


                        if (myCreature != null && myCreature.isAlive()) {
                            Iterator<Map.Entry<Integer, Equipment>> equipmentHashIterator = equipmentHashMap.entrySet().iterator();
                            while (equipmentHashIterator.hasNext()) {
                                Map.Entry<Integer, Equipment> equipmentMap = equipmentHashIterator.next();
                                int equipmentKey = equipmentMap.getKey();
                                Equipment equipment = equipmentMap.getValue();
                                if (equipment.getImageView().getBoundsInParent().intersects(myCreature.getCreatureImageView().getBoundsInParent())) {
                                    new EquipmentRequestMsg(campType, myCreature.getCreatureId(), equipmentKey).sendMsg(out);
                                }
                            }
                        }



                        for(Equipment equipment : equipmentHashMap.values()) {
                            equipment.draw();
                        }
                        for (Creature myMember : myFamily.values()) {
                            myMember.sendAllAttribute(out);
                        }
                        Thread.sleep(Constant.FRAME_TIME);
                    } catch (Exception e) {
                        System.out.println("while(true)出错");
                        e.printStackTrace();
                    } finally {
                        //flag = !flag;
                    }
                }
            }
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                for (Creature myMember : myFamily.values()) {
//                                    ArrayList<Bullet> tempBullet = myMember.update();
////                                    if (tempBullet.size() != 0) {
////                                        bullets.addAll(tempBullet);
////                                        Iterator<Bullet> bulletIterator = tempBullet.listIterator();
////                                        while (bulletIterator.hasNext()) {
////                                            Bullet bullet = bulletIterator.next();
////                                            if (bullet.getBulletType() == Constant.REMOTE_BULLET_TYPE)
////                                                sceneController.getMapPane().getChildren().add(bullet.getCircleShape());
////                                        }
////                                    }
//                                }
////                                for (Creature enemyMember : enemyFamily.values()) {
////                                     // TODO: 改成接受服务器的消息进行变动
////                                    ArrayList<Bullet> tempBullet = enemyMember.update();
////                                    if (tempBullet.size() != 0) {
////                                        bullets.addAll(tempBullet);
////                                        Iterator<Bullet> bulletIterator = tempBullet.listIterator();
////                                        while (bulletIterator.hasNext()) {
////                                            Bullet bullet = bulletIterator.next();
////                                            if (bullet.getBulletType() == Constant.REMOTE_BULLET_TYPE)
////                                                sceneController.getMapPane().getChildren().add(bullet.getCircleShape());
////                                        }
////                                    }
////                                }
////                                Iterator<Bullet> bulletIterator = bullets.listIterator();
////                                while (bulletIterator.hasNext()) {
////                                    Bullet bullet = bulletIterator.next();
////                                    if (bullet.isValid()) {
////                                        Collision collision = bullet.update();
////                                        if (collision != null) {
////                                            collision.collisionEvent();
////                                            bulletIterator.remove();
////                                            if (bullet.getBulletType() == Constant.REMOTE_BULLET_TYPE) {
////                                                Platform.runLater(new Runnable() {
////                                                    @Override
////                                                    public void run() {
////                                                        sceneController.getMapPane().getChildren().remove(bullet.getCircleShape());
////                                                    }
////                                                });
////                                            }
////                                        }
////                                    }
////                                }
////                                if (equipmentFactory.hasNext()) {
////                                    Equipment equipment = equipmentFactory.next();
////                                    if (equipment != null)
////                                        equipmentList.add(equipment);
////                                }
////                                if (myCreature != null && myCreature.isAlive()) {
////                                    Iterator<Equipment> equipmentIterator = equipmentList.listIterator();
////                                    while (equipmentIterator.hasNext()) {
////                                        Equipment equipment = equipmentIterator.next();
////                                        if (equipment.getImageView().getBoundsInParent().intersects(myCreature.getCreatureImageView().getBoundsInParent())) {
////                                            myCreature.pickUpEquipment(equipment);
////                                            equipmentIterator.remove();
////                                        }
////                                    }
////                                }
////                                for (Equipment equipment : equipmentList) {
////                                    equipment.draw();
////                                }
//                            }
//                        });
//                        for(Creature myMember : myFamily.values()) {
//                            myMember.sendAllAttribute();
//                        }
//                        Thread.sleep(Constant.FRAME_TIME);
//                    } catch (Exception e) {
//                        System.out.println("while(true)出错");
//                        e.printStackTrace();
//                    } finally {
//                        //flag = !flag;
//                    }
//                }
//            }
//        }).start();
    }

    private void init(String camp, HashMap<Integer, Creature> myFamily, HashMap<Integer, Creature> enemyFamily) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(camp.equals(Constant.CampType.GOURD)) {
                    myCreatureImageView.setLayoutX(5);
                    enemyCreatureImageView.setLayoutX(5);
                    myCreatureText.setLayoutX(5);
                    enemyCreatureText.setLayoutX(5);
                } else {
                    myCreatureImageView.setLayoutX(5 + Constant.SCENE_MARGIN_SIZE + Constant.FIGHT_PANE_WIDTH);
                    enemyCreatureImageView.setLayoutX(5 + Constant.SCENE_MARGIN_SIZE + Constant.FIGHT_PANE_WIDTH);
                    myCreatureText.setLayoutX(5 + Constant.SCENE_MARGIN_SIZE + Constant.FIGHT_PANE_WIDTH);
                    enemyCreatureText.setLayoutX(5 + Constant.SCENE_MARGIN_SIZE + Constant.FIGHT_PANE_WIDTH);
                }
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (myCreature == null || !myCreature.isAlive()) {
                                myCreatureImageView.setVisible(false);
                                myCreatureImageView.setDisable(true);
                                myCreatureText.setVisible(false);
                                enemyCreatureImageView.setVisible(false);
                                enemyCreatureImageView.setDisable(true);
                                enemyCreatureText.setVisible(false);
                            } else {
                                myCreatureImageView.setVisible(true);
                                myCreatureImageView.setDisable(false);
                                int id = myCreature.getCreatureId();
                                if (camp.equals(Constant.CampType.GOURD)) {
                                    myCreatureImageView.setImage(ImageUrl.gourdLeftImageMap.get(id));
                                } else
                                    myCreatureImageView.setImage(ImageUrl.monsterLeftImageMap.get(id));
                                myCreatureText.setText(myCreature.showMessage());
                                myCreatureText.setLayoutY(20 + myCreatureImageView.getBoundsInLocal().getMaxY() + 20);
                                myCreatureText.setVisible(true);
                                if (enemyCreature == null || !enemyCreature.isAlive()) {
                                    enemyCreatureImageView.setVisible(false);
                                    enemyCreatureImageView.setDisable(true);
                                    enemyCreatureText.setVisible(false);
                                } else {
                                    enemyCreatureImageView.setVisible(true);
                                    enemyCreatureImageView.setDisable(false);
                                    enemyCreatureImageView.setLayoutY(20 + myCreatureImageView.getBoundsInLocal().getMaxY()
                                            + 20 + myCreatureText.getBoundsInLocal().getMaxY() + 20);
                                    int id0 = enemyCreature.getCreatureId();
                                    if (camp.equals(Constant.CampType.GOURD)) {
                                        if (myCreature.getCreatureId() == CreatureId.GRANDPA_ID)
                                            enemyCreatureImageView.setImage(ImageUrl.gourdLeftImageMap.get(id0));
                                        else
                                            enemyCreatureImageView.setImage(ImageUrl.monsterLeftImageMap.get(id0));
                                    } else
                                        enemyCreatureImageView.setImage(ImageUrl.gourdLeftImageMap.get(id0));
                                    enemyCreatureText.setText(enemyCreature.showMessage());
                                    enemyCreatureText.setLayoutY(20 + myCreatureImageView.getBoundsInLocal().getMaxY()
                                            + 20 + myCreatureText.getBoundsInLocal().getMaxY() + 20 +
                                            enemyCreatureImageView.getBoundsInLocal().getMaxY() + 20);
                                    enemyCreatureText.setVisible(true);
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        int idOffset = 0;
        if (!camp.equals(Constant.CampType.GOURD))
            idOffset = CreatureId.MIN_MONSTER_ID;
//        for (Creature creature : myFamily.values()) {
//            ImageView imageView = creature.getCreatureImageView();
//            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    if (myCreature == null || !myCreature.isAlive()) {
//                        myCreature = creature;
//                        myCreature.flipControlled();
//                    } else if (myCreature != creature) {
//                        myCreature.flipControlled();
//                        myCreature = creature;
//                        myCreature.flipControlled();
//                    } else if (!myCreature.isControlled()) {
//                        myCreature.flipControlled();
//                    }
//                }
//            });
//        }
        for (Creature creature : myFamily.values()) {
            ImageView imageView = creature.getCreatureImageView();
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if (enemyCreature != creature && myCreature != null && myCreature.isAlive()
                            && myCreature.getCreatureId() == CreatureId.GRANDPA_ID) {
                        enemyCreature = creature;
                        myCreature.setPlayerAttackTarget(enemyCreature);
                    }
//                    if (myCreature == null || !myCreature.isAlive()) {
//                        myCreature = creature;
//                        myCreature.flipControlled();
//                    } else if (myCreature != creature) {
//                        myCreature.flipControlled();
//                        myCreature = creature;
//                        myCreature.flipControlled();
//                    } else if (!myCreature.isControlled()) {
//                        myCreature.flipControlled();
//                    }
                }
            });
        }
        for (Creature creature : enemyFamily.values()) {
            ImageView imageView = creature.getCreatureImageView();
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if (enemyCreature != creature && myCreature != null && myCreature.isAlive()
                            && myCreature.getCreatureId() != CreatureId.GRANDPA_ID) {
                        enemyCreature = creature;
                        myCreature.setPlayerAttackTarget(enemyCreature);
                    }
                }
            });
        }


        sceneController.getMapPane().setFocusTraversable(true);

        final boolean[] isUpPressOn = {false};
        final boolean[] isDownPressOn = {false};
        final boolean[] isLeftPressOn = {false};
        final boolean[] isRightPressOn = {false};
        final int[] lastPressOn = {Constant.Direction.STOP};
        int finalIdOffset = idOffset;
        sceneController.getMapPane().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.W) {
                    isUpPressOn[0] = true;
                    lastPressOn[0] = Constant.Direction.UP;
                } else if (keyCode == KeyCode.S) {
                    isDownPressOn[0] = true;
                    lastPressOn[0] = Constant.Direction.DOWN;
                } else if (keyCode == KeyCode.A) {
                    isLeftPressOn[0] = true;
                    lastPressOn[0] = Constant.Direction.LEFT;
                } else if (keyCode == KeyCode.D) {
                    isRightPressOn[0] = true;
                    lastPressOn[0] = Constant.Direction.RIGHT;
                } else if (keyCode == KeyCode.Q) {
                    if (myCreature != null && myCreature.isAlive()) {
                        myCreature.setQFlag(true);
                    }
                } else if (keyCode == KeyCode.E) {
                    if (myCreature != null && myCreature.isAlive())
                        myCreature.setEFlag(true);
                } else if (keyCode == KeyCode.R) {
                    if (myCreature != null && myCreature.isAlive())
                        myCreature.setRFlag(true);
                } else if (keyCode == KeyCode.SPACE) {
                    if (myCreature != null && myCreature.isControlled()) {
                        myCreature.flipControlled();
                        myCreature = null;
                    }
                }
                if (myCreature != null) {
                    if (isLeftPressOn[0] || isRightPressOn[0] || isUpPressOn[0] || isDownPressOn[0])
                        myCreature.setDirection(lastPressOn[0]);
                    else
                        myCreature.setDirection(Constant.Direction.STOP);
                }
                if (keyCode.isDigitKey()) {
                    int num = keyCode.ordinal() - 25;
                    System.out.println(num);
                    if (0 <= num && num <= 8) {
                        Creature creature = myFamily.get(finalIdOffset + num);
                        if (myCreature != creature) {
                            if (creature != null && creature.isAlive()) {
                                if (myCreature != null && myCreature.isAlive() && myCreature.isControlled())
                                    myCreature.flipControlled();
                                if (!creature.isControlled())
                                    creature.flipControlled();
                                myCreature = creature;
                                enemyCreature = null;
                            }
                        }
                    }
                }
            }
        });

        sceneController.getMapPane().setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.W)
                    isUpPressOn[0] = false;
                else if (keyCode == KeyCode.S)
                    isDownPressOn[0] = false;
                else if (keyCode == KeyCode.A)
                    isLeftPressOn[0] = false;
                else if (keyCode == KeyCode.D)
                    isRightPressOn[0] = false;
                if (myCreature != null) {
                    if (isLeftPressOn[0] || isRightPressOn[0] || isUpPressOn[0] || isDownPressOn[0])
                        myCreature.setDirection(lastPressOn[0]);
                    else
                        myCreature.setDirection(Constant.Direction.STOP);
                }
            }
        });

    }

}