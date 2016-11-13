import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by 14366 on 2016/10/20.
 */
public class AnimalChess {

    public static void main(String[] args) throws FileNotFoundException {
        //读入地图文件,并且将其储存到二维数组中.
        char[][] map = new char[7][9];
        char[][] animalMap = new char[7][9];
        Scanner input = new Scanner(System.in);
        File tile = new File("tile.txt");
        Scanner scanner1 = new Scanner(tile);
        File animal = new File("animal.txt");
        Scanner scanner2 = new Scanner(animal);
        String map1 = scanner1.nextLine();
        String animalmap = scanner2.nextLine();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                map[i][j] = map1.charAt(9 * i + j);
                animalMap[i][j] = animalmap.charAt(9 * i + j);
            }
            System.out.println();
        }

        System.out.println("欢迎来到斗兽棋游戏!");
        System.out.println("指令介绍:" + "\n" + "1.游戏指令" + "\n" + "    restart重新开始游戏" + "\n"
                + "    help查看帮助" + "\n" + "    undo悔棋" + "\n" + "    redo取消悔棋" + "\n"
                + "    exit退出游戏" + "\n" + "2.移动指令" + "\n" + "    移动指令由两个部分组成" + "\n"
                + "    第一个部分是数字1-8,根据战斗力分别对应鼠(1),猫(2),狼(3),狗(4),豹(5),虎(6),狮(7),象(8)"
                + "\n" + "    第二个部分是字母wasd中的一个,其中w对应上方向,a对应左方向,s对应下方向,d对应右方向" + "\n"
                + "    如1d表示鼠向右走,4w表示狗向上走" + "\n");

        printMap(animalMap, map);
        boolean player = true;
        System.out.println("如果需要帮助,请随时输入help来查看.");
        char[][][] memory = new char[10000][7][9];
        int currentStep = 0, lastStep = 0, nextStep = 0;

        origin:
        for (int q = 0; ; q++) {
            memory[currentStep] = copyArray(animalMap);
           //判断哪方下棋或者是否有人获胜,并据此打印输入提示语.
            if (judgingWin(animalMap, map) == 1) {
                System.out.println("您已获胜,游戏结束!");
                System.out.println("请输入restart重新开始游戏!");
            }
            if (player) {
                System.out.println("左方棋子行动:");
            } else {
                System.out.println("右方棋子行动:");
            }

            String command = input.nextLine();
            System.out.println(command);

            if (command.equals("help")) {
                System.out.println("指令介绍:" + "\n" + "1.游戏指令" + "\n" + "    restart重新开始游戏" + "\n" + "    help查看帮助" + "\n"
                        + "    undo悔棋" + "\n" + "    redo取消悔棋" + "\n" + "    exit退出游戏" + "\n" + "2.移动指令" + "\n" + "    移动指令由两个部分组成" + "\n" +
                        "    第一个部分是数字1-8,根据战斗力分别对应鼠(1),猫(2),狼(3),狗(4),豹(5),虎(6),狮(7),象(8)" + "\n" + "    第二个部分是字母wasd中的一个,其中w对应上方向,a对应左方向,s对应下方向,d对应右方向" + "\n"
                        + "    如1d表示鼠向右走,4w表示狗向上走" + "\n");
                continue origin;
            }

            if (command.equals("restart")) {
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 9; j++) {
                        animalMap[i][j] = memory[0][i][j];
                    }
                }
                printMap(animalMap, map);
                player = true;
                continue origin;
            }

            if (command.equals("exit")) {
                break;
            }

            if (command.equals("undo")) {
                nextStep = currentStep - 1;
                //判断若已经回到开头,则不打印棋盘.
                if (currentStep == undo(currentStep, nextStep)) {
                    continue origin;
                } else {
                    currentStep = undo(currentStep, nextStep);
                    animalMap = copyArray(memory[currentStep]);
                    printMap(animalMap, map);
                    player = !player;
                    continue origin;
                }
            }

            if (command.equals("redo")) {
                nextStep = currentStep + 1;
                if (nextStep > lastStep) {
                    System.out.println("已经回到最后的记录,不能再取消悔棋了!");
                    continue origin;
                } else {
                    currentStep = redo(currentStep, lastStep, nextStep);
                    animalMap = copyArray(memory[currentStep]);
                    printMap(animalMap, map);
                    player = !player;
                    continue origin;
                }
            }

            if (command.length() < 2) {
                System.out.println("不能识别该指令,请重新输入!");
                continue origin;
            } else if (command.length() > 2) {
                System.out.println("不能识别该指令,请重新输入!");
                continue origin;
            }

            char animals = command.charAt(0);
            //输入的动物必须存在且未被吃掉.
            if ((0 <= animals - 'a' & animals-'a' < 8) | (0 <= animals - '1' & animals - '1' < 8)) {
                if (findPosition(player, animals, animalMap).equals("had been eaten")) {
                    System.out.println("您的该动物已被吃掉!");
                    continue origin;
                }
            } else {
                System.out.println("不存在您输入的动物,请重新输入!");
                continue origin;
            }

            //确定动物所在的行列坐标
            int line = findPosition(player, animals, animalMap).charAt(0) - '0';
            int row = findPosition(player, animals, animalMap).charAt(1) - '0';
            switch (command.charAt(1)) {
                /**
                 * 按照朝各个方向行走来分类,
                 *看每个具体的情况能不能走.
                 * 若不能走,回到最初的循环.
                 *若能走,每次改变了地图数据后要判断是否胜利*/
                case 'w':
                    if (line - 1 < 0) {
                        System.out.println("棋子不能走出棋盘边界,请重新输入");
                        continue origin;
                    }
                    /**
                     * 向上走时动物遇到水的情况
                     * 1不能下水的动物.
                     * 2可以跳河的狮虎判断对岸的具体情况.
                     * 3中间有对方的老鼠.
                     * 向其他方向时以此类推.
                     */
                    switch (meetWater(animalMap, line, row, command.charAt(1), map, player)) {
                        case 1:
                            System.out.println("该动物不能下水!");
                            continue origin;
                        case 2:
                            if (player) {
                                if (animalMap[line - 3][row] == '0' | animalMap[line][row] - 'a' >= animalMap[line - 3][row] - '1') {
                                    animalMap[line - 3][row] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            } else {
                                if (animalMap[line][row] == '0' | animalMap[line][row] - '1' >= animalMap[line - 3][row] - 'a') {
                                    animalMap[line - 3][row] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            }
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                continue origin;
                            }
                        case 3:
                            System.out.println("对方鼠挡住去路,不能飞过河!");
                            continue origin;
                        default:
                            System.out.println();
                    }/**
                      * 鼠在河里向上走的情况
                      * 去到的位置没有动物
                      * 河里的鼠不能吃岸上的大象
                      * 河里的鼠能吃岸上的鼠
                      *鼠战斗力小于其他动物
                      */
                    if ((animalMap[line][row] == 'a' | animalMap[line][row] == '1') & (line == 1 | line == 4) & (map[line][row] == '1')) {
                        if (animalMap[line - 1][row] == '0') {
                            animalMap[line - 1][row] = animalMap[line][row];
                            animalMap[line][row] = '0';
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                continue origin;
                            }
                            continue origin;
                        } else if ((animalMap[line][row] == 'a' & animalMap[line - 1][row] == '8') | (animalMap[line][row] == '1' & animalMap[line - 1][row] == 'h')) {
                            System.out.println("河里的鼠不能吃岸上的象!");
                            continue origin;
                        } else if (animalMap[line - 1][row] != '0') {
                            if ((animalMap[line][row] == 'a' & animalMap[line - 1][row] == '1') | (animalMap[line][row] == '1' & animalMap[line - 1][row] == 'a')) {
                                animalMap[line - 1][row] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                            } else {
                                System.out.println("该位置已被占据,您的鼠不能落在该位置");
                                continue origin;
                            }
                        }
                    } else {
                        /**
                         * 不涉及水的正常移动
                         * 动物不能进入己方的家
                         * 不能吃己方的棋子
                         * 不能吃战斗力比自己强的棋子
                         */
                        switch (whetherMove(animalMap[line][row], animalMap[line - 1][row], map[line - 1][row], player)) {
                            case 1:
                                System.out.println("不能进入己方的家!");
                                continue origin;

                            case 2:
                                System.out.println("不能吃己方的棋子!");
                                continue origin;

                            case 3:
                                System.out.println("赶紧跑吧!对方的棋子战斗力比你强哦!");
                                continue origin;

                            default:
                                animalMap[line - 1][row] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                        }
                    }
                    break;

                case 's':
                    if (line + 1 > 6) {
                        System.out.println("棋子不能走出棋盘边界,请重新输入");
                        continue origin;
                    }
                    switch (meetWater(animalMap, line, row, command.charAt(1), map, player)) {
                        case 1:
                            System.out.println("该动物不能下水!");
                            continue origin;
                        case 2:
                            if (player) {
                                if (animalMap[line + 3][row] == '0' | animalMap[line][row] - 'a' >= animalMap[line + 3][row] - '1') {
                                    animalMap[line + 3][row] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            } else {
                                if (animalMap[line + 3][row] == '0' | animalMap[line][row] - '1' >= animalMap[line + 3][row] - 'a') {
                                    animalMap[line + 3][row] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            }
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                break origin;
                            }
                            continue origin;
                        case 3:
                            System.out.println("对方鼠挡住去路,不能飞过河!");
                            continue origin;
                        default:
                            System.out.println();
                    }
                    if ((animalMap[line][row] == 'a' | animalMap[line][row] == '1') && (line == 2 | line == 5) & (map[line][row] == '1')) {
                        if (animalMap[line + 1][row] == '0') {
                            animalMap[line + 1][row] = animalMap[line][row];
                            animalMap[line][row] = '0';
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                break origin;
                            }
                            continue origin;
                        } else if ((animalMap[line][row] == 'a' & animalMap[line + 1][row] == '8') | (animalMap[line][row] == '1' & animalMap[line + 1][row] == 'h')) {
                            System.out.println("河里的鼠不能吃岸上的象!");
                            continue origin;
                        } else if (animalMap[line + 1][row] != '0') {
                            if ((animalMap[line][row] == 'a' & animalMap[line + 1][row] == '1') | (animalMap[line][row] == '1' & animalMap[line + 1][row] == 'a')) {
                                animalMap[line + 1][row] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    break origin;
                                }
                                continue origin;
                            } else {
                                System.out.println("该位置已被占据,您的鼠不能落在该位置");
                                continue origin;
                            }
                        }
                    } else {
                        switch (whetherMove(animalMap[line][row], animalMap[line + 1][row], map[line + 1][row], player)) {
                            case 1:
                                System.out.println("不能进入己方的家!");
                                continue origin;

                            case 2:
                                System.out.println("不能吃己方的棋子!");
                                continue origin;

                            case 3:
                                System.out.println("赶紧跑吧!对方的棋子战斗力比你强哦!");
                                continue origin;
                            default:
                                animalMap[line + 1][row] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                        }
                    }
                    break;

                case 'a':
                    if (row - 1 < 0) {
                        System.out.println("棋子不能走出棋盘边界,请重新输入");
                        continue origin;
                    }
                    switch (meetWater(animalMap, line, row, command.charAt(1), map, player)) {
                        case 1:
                            System.out.println("该动物不能下水!");
                            continue origin;
                        case 2:
                            if (player) {
                                if (animalMap[line][row - 4] == '0' | animalMap[line][row] - 'a' >= animalMap[line][row - 4] - '1') {
                                    animalMap[line][row - 4] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            } else {
                                if (animalMap[line][row - 4] == '0' | animalMap[line][row] - '1' >= animalMap[line][row - 4] - 'a') {
                                    animalMap[line][row - 4] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            }
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                continue origin;
                            }
                            continue origin;
                        case 3:
                            System.out.println("对方鼠挡住去路,不能飞过河!");
                            continue origin;
                        default:
                            System.out.println();
                    }
                    if ((animalMap[line][row] == 'a' | animalMap[line][row] == '1') && (row == 3 & map[line][row] == '1')) {
                        if (animalMap[line][row - 1] == '0') {
                            animalMap[line][row - 1] = animalMap[line][row];
                            animalMap[line][row] = '0';
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                continue origin;
                            }
                            continue origin;
                        } else if ((animalMap[line][row] == 'a' & animalMap[line][row - 1] == '8') | (animalMap[line][row] == '1' & animalMap[line][row - 1] == 'h')) {
                            System.out.println("河里的鼠不能吃岸上的象!");
                            continue origin;
                        } else if (animalMap[line][row - 1] != 0) {
                            if ((animalMap[line][row] == 'a' & animalMap[line][row - 1] == '1') | (animalMap[line][row] == '1' & animalMap[line][row - 1] == 'a')) {
                                animalMap[line][row - 1] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                            } else {
                                System.out.println("该位置已被占据,您的鼠不能落在该位置");
                                continue origin;
                            }
                        }
                    } else {
                        switch (whetherMove(animalMap[line][row], animalMap[line][row - 1], map[line][row - 1], player)) {
                            case 1:
                                System.out.println("不能进入己方的家!");
                                continue origin;

                            case 2:
                                System.out.println("不能吃己方的棋子!");
                                continue origin;

                            case 3:
                                System.out.println("赶紧跑吧!对方的棋子战斗力比你强哦!");
                                continue origin;

                            case 0:
                                animalMap[line][row - 1] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                        }
                    }
                    break;

                case 'd':
                    if (row + 1 > 8) {
                        System.out.println("棋子不能走出棋盘边界,请重新输入");
                        continue origin;
                    }
                    switch (meetWater(animalMap, line, row, command.charAt(1), map, player)) {
                        case 1:
                            System.out.println("该动物不能下水!");
                            continue origin;
                        case 2:
                            if (player) {
                                if (animalMap[line][row + 4] == '0' | animalMap[line][row] - 'a' >= animalMap[line + 4][row] - '1') {
                                    animalMap[line][row + 4] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            } else {
                                if (animalMap[line][row + 4] == '0' | animalMap[line][row] - '1' >= animalMap[line][row + 4] - 'a') {
                                    animalMap[line][row + 4] = animalMap[line][row];
                                    animalMap[line][row] = '0';
                                } else {
                                    System.out.println("对岸的动物比您强大,无法跳河!");
                                    continue origin;
                                }
                            }
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                continue origin;
                            }
                            continue origin;
                        case 3:
                            System.out.println("对方鼠挡住去路,不能飞过河!");
                            continue origin;
                        default:
                    }
                    if ((animalMap[line][row] == 'a' | animalMap[line][row] == '1') && (row == 5 & map[line][row] == '1')) {
                        if (animalMap[line][row + 1] == '0') {
                            animalMap[line][row + 1] = animalMap[line][row];
                            animalMap[line][row] = '0';
                            printMap(animalMap, map);
                            lastStep = ++currentStep;
                            player = !player;
                            if (judgingWin(animalMap, map) == 1) {
                                continue origin;
                            }
                            continue origin;
                        } else if ((animalMap[line][row] == 'a' & animalMap[line][row + 1] == '8') | (animalMap[line][row] == '1' & animalMap[line][row + 1] == 'h')) {
                            System.out.println("河里的鼠不能吃岸上的象!");
                            continue origin;
                        } else if (animalMap[line][row + 1] != 0) {
                            if ((animalMap[line][row] == 'a' & animalMap[line][row + 1] == '1') | (animalMap[line][row] == '1' & animalMap[line][row + 1] == 'a')) {
                                animalMap[line][row + 1] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                printMap(animalMap, map);
                                lastStep = ++currentStep;
                                player = !player;
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                            } else {
                                System.out.println("该位置已被占据,您的鼠不能落在该位置");
                                continue origin;
                            }
                        }
                    } else {
                        switch (whetherMove(animalMap[line][row], animalMap[line][row + 1], map[line][row + 1], player)) {
                            case 1:
                                System.out.println("不能进入己方的家!");
                                continue origin;
                            case 2:
                                System.out.println("不能吃己方的棋子!");
                                continue origin;
                            case 3:
                                System.out.println("赶紧跑吧!对方的棋子战斗力比你强哦!");
                                continue origin;
                            default:
                                animalMap[line][row + 1] = animalMap[line][row];
                                animalMap[line][row] = '0';
                                lastStep = ++currentStep;
                                player = !player;
                                printMap(animalMap, map);
                                if (judgingWin(animalMap, map) == 1) {
                                    continue origin;
                                }
                                continue origin;
                        }
                    }
                    break;
                default:
                    System.out.println("不能识别该指令,请重新输入!");
                    continue origin;
            }
        }
    }

    //在每次棋盘状态发生变化时打印地图
    public static void printMap(char[][] animalMap, char[][] map) throws FileNotFoundException {

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                if (animalMap[i][j] != '0') {
                    switch (animalMap[i][j]) {
                        case 'a':
                            System.out.print("1鼠 ");
                            break;
                        case 'b':
                            System.out.print("2猫 ");
                            break;
                        case 'c':
                            System.out.print("3狼 ");
                            break;
                        case 'd':
                            System.out.print("4狗 ");
                            break;
                        case 'e':
                            System.out.print("5豹 ");
                            break;
                        case 'f':
                            System.out.print("6虎 ");
                            break;
                        case 'g':
                            System.out.print("7狮 ");
                            break;
                        case 'h':
                            System.out.print("8象 ");
                            break;
                        case '1':
                            System.out.print("鼠1 ");
                            break;
                        case '2':
                            System.out.print("猫2 ");
                            break;
                        case '3':
                            System.out.print("狼3 ");
                            break;
                        case '4':
                            System.out.print("狗4 ");
                            break;
                        case '5':
                            System.out.print("豹5 ");
                            break;
                        case '6':
                            System.out.print("虎6 ");
                            break;
                        case '7':
                            System.out.print("狮7 ");
                            break;
                        case '8':
                            System.out.print("象8 ");
                            break;
                        default:
                            System.out.print("");
                            break;
                    }
                } else {
                    switch (map[i][j]) {
                        case '0':
                            System.out.print(" 　 ");
                            break;
                        case '1':
                            System.out.print(" 水 ");
                            break;
                        case '2':
                            System.out.print(" 陷 ");
                            break;
                        case '3':
                            System.out.print(" 家 ");
                            break;
                        case '4':
                            System.out.print(" 陷 ");
                            break;
                        case '5':
                            System.out.print(" 家 ");
                            break;
                        default:
                            System.out.print("");
                            break;
                    }
                }
            }
            System.out.println();

        }
    }

    /**
     * 判断是否能够进行悔棋,且确定悔棋后棋盘到达哪一步的状态
     * currentStep 是当前棋盘有效的步数
     * nextStep悔棋之后想要去到的步数
     * 若可以悔棋则返回悔棋后的currentStep以便据此来打印最新的棋盘*/
    private static int undo(int currentStep, int nextStep) {
        if (nextStep >= 0) {
            currentStep = nextStep;
        } else {
            System.out.println("已经退回开局,没有可悔的操作!");
        }
        return currentStep;
    }

    /**
     * 判断是否能够撤销悔棋且确定悔棋之后棋盘到达哪一步的状态
     * currentStep 当前棋盘的有效步数
     * lastStep 在悔棋前正常下棋所达到的最大步数
     * nextStep 撤销悔棋是想要去到的步数
     * 若可以撤销悔棋则返回撤销悔棋的currentStep以便据此打印最新的棋盘*/
    private static int redo(int currentStep, int lastStep, int nextStep) {
        if (nextStep <= lastStep) {
            currentStep = nextStep;
        }
        return currentStep;
    }

    /**
     * 确定想要操作的棋子所在的位置的行和列
     * player确定下棋的是左方或者右方,若是左方要把玩家输入的数字先转化为对应的字母
     * theAnimal是玩家输入的数字,对应相应的动物
     * animal[][]里面储存了动物的数据
     * 返回值里面是该动物的行列坐标*/
    public static String findPosition(boolean players, char theAnimal, char[][] animal) {
        String position = "";
        if (players) {
            theAnimal = (char) (theAnimal - '1' + 'a');
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                if (animal[i][j] == theAnimal) {
                    position = position + i + j;
                    return position;
                }
            }
        }
        return "had been eaten";
    }

    /**
     *判断动物遇水之后的运动
     * animal[][]里储存了动物的数据
     * map[][]储存了地形的数据
     * line和row分别是该动物对应的行列坐标
     * direction是玩家输入的操作方向
     * player表示哪方玩家操作
     */
    /**返回值
     * 1表示该动物不能下水
     * 2表示该动物是可以跳河的狮虎且中间无对方鼠阻挡
     * 3表示被敌方的鼠挡住去路*/
    public static int meetWater(char[][] animal, int line, int row, char direction, char[][] map, boolean players) {
        if (animal[line][row] != 'a' && animal[line][row] != '1') {
            switch (direction) {
                case 'w':
                    if (map[line - 1][row] == '1') {
                        if (animal[line][row] != '6' && animal[line][row] != '7' && animal[line][row] != 'f' && animal[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((players == true && animal[line - 1][row] != '1' && animal[line - 2][row] != '1') |
                                    (players == false && animal[line - 1][row] != 'a' && animal[line - 2][row] != 'a')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
                case 's':
                    if (map[line + 1][row] == '1') {
                        if (animal[line][row] != '6' && animal[line][row] != '7' && animal[line][row] != 'f' && animal[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((players == true && animal[line + 1][row] != '1' && animal[line + 2][row] != '1') |
                                    (players == false && animal[line + 1][row] != 'a' && animal[line + 2][row] != 'a')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
                case 'a':
                    if (map[line][row - 1] == '1') {
                        if (animal[line][row] != '6' && animal[line][row] != '7' && animal[line][row] != 'f' && animal[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((players == true && animal[line][row - 1] != '1' && animal[line][row - 2] != '1' && animal[line][row - 3] != '1') |
                                    (players == false && animal[line][row - 1] != 'a' && animal[line][row - 2] != 'a' && animal[line][row - 3] != 'a')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
                case 'd':
                    if (map[line][row + 1] == '1') {
                        if (animal[line][row] != '6' && animal[line][row] != '7' && animal[line][row] != 'f' && animal[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((players == true && animal[line][row + 1] != '1' && animal[line][row + 2] != '1' && animal[line][row + 3] != '1') |
                                    (players == false && animal[line][row + 1] != 'a' && animal[line][row + 2] != 'a' && animal[line][row + 3] != 'a')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
            }
        }
        return 0;
    }

    /**判断动物的正常运动
     * animal1代表当前要操作的动物
     * animal2代表要去的位置的动物
     * map[][]储存着地形
     * player表示哪方玩家操作*/
    /**
     * 返回值
     * 0代表可以走
     * 1代表将要走入己方兽穴
     * 2代表将要吃自己的动物
     * 3代表比要去的位置的动物战斗力弱*/
    public static int whetherMove(char animal1, char animal2, char map, boolean player) {
        if (player) {
            if (map == '3') {
                return 1;
            }
            if (animal2 - 'a' >= 0 & animal2 - 'a' < 8) {
                return 2;
            }
            if (animal2 != '0') {
                if (map == '2') {
                    return 0;
                } else if ((animal1 - 'a' < animal2 - '1' & !(animal1 == 'a' & animal2 == '8')) | (animal1 == 'h' & animal2 == '1')) {
                    return 3;
                } else return 0;
            } else if (animal2 == '0') {
                return 0;
            }
        }
        if (!player) {
            if (map == '5') {
                return 1;
            }
            if (animal2 - '1' >= 0 & animal2 - '1' < 8) {
                return 2;
            } else if (animal2 != '0') {
                if (map == '4') {
                    return 0;
                } else if ((animal1 - '1' < animal2 - 'a' & !(animal1 == '1' & animal2 == 'h')) | (animal1 == '8' & animal2 == 'a')) {
                    return 3;
                } else return 0;
            } else if (animal2 == '0')
                return 0;
        }
        return 0;
    }

    /**
     * 判断胜负中因占领对方兽穴和吃光对方棋子赢得的胜利
     * animal[][]储存动物数据
     * map[][]储存着地图数据
     */
    /**
     * 返回值
     * 1 表示已经胜利
     * 0只是为了能够编译通过*/
    public static int judgingWin(char animal[][], char map[][]) {
        if (animal[3][0] != '0' | animal[3][8] != '0') {
            return 1;
        } else {
            int num1 = 0, num2 = 0;
            for (int m = 0; m < 8; m++) {
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (animal[i][j] == (char) (m + '1')) {
                            num1++;
                        }
                        if (animal[i][j] == (char) (m + 'a')) {
                            num2++;
                        }
                    }
                }
            }
            if (num1 == 0 | num2 == 0) {

                return 1;
            } else return 0;
        }
    }

    /**把每次操作之后的棋盘状态保存下来*/
    public static char[][] copyArray(char[][] animal) {
        char[][] array1 = new char[7][9];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                array1[i][j] = animal[i][j];
            }
        }
        return array1;
    }
}








