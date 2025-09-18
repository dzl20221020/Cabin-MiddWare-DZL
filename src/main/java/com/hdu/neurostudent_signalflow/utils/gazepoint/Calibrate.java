package com.hdu.neurostudent_signalflow.utils.gazepoint;

import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Calibrate {

//    private static Vector<Point2D.Double> calibpts = new Vector<>();
    // 校准点坐标
    private static List<double[]> calibpts = new ArrayList<>();
    private static int numpts;
    Socket socket = null;

    public Calibrate() {
        // Add calibration points
        calibpts.add(new double[]{0.5,0.5});
        calibpts.add(new double[]{0.85,0.15});
        calibpts.add(new double[]{0.85,0.85});
        calibpts.add(new double[]{0.15,0.85});
        calibpts.add(new double[]{0.15,0.15});

        // Start calibration
        try {
            socket = new Socket("127.0.0.1", 4242);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean start() {
        numpts = calibpts.size();

        try {
            // Send calibration delay command
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("<SET ID=\"CALIBRATE_DELAY\" VALUE=\"1.5\" />\r\n".getBytes());
            outputStream.flush();

            if (!waitForResponse(socket)) return false;

            // Draw calibration points
            for (int i = 0; i < numpts; ++i) {
                outputStream.write(String.format("<SET ID=\"CALIBRATE_ADDPOINT\" X=\"%f\" Y=\"%f\" />\r\n", calibpts.get(i)[0], calibpts.get(i)[1]).getBytes());
                outputStream.flush();
                if (!waitForResponse(socket)) return false;
            }

            outputStream.write("<SET ID=\"CALIBRATE_SHOW\" STATE=\"1\" />\r\n".getBytes());
            outputStream.write("<SET ID=\"CALIBRATE_START\" STATE=\"1\" />\r\n".getBytes());
            outputStream.flush();

            // 获得服务器的输入流
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

           //收到数据
            String line;
            try {
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (SocketTimeoutException e) {
                System.err.println("读取数据超时，可能是因为没有数据到达。");
                // 根据需要进行重新连接或其他处理
            } catch (IOException e) {
                System.err.println("读取数据时发生IO异常。");
                e.printStackTrace();
            }

            //判断结果
            outputStream.write("<GET ID=\"CALIBRATE_RESULT_SUMMARY\" />\r\n".getBytes());

            line = input.readLine();
            if (line != null){
                System.out.println(line);
                Element root = Xmlutil.parseXml(line);
                // 检查根元素是否是 <ACK> 标签
                if (root.getTagName().equalsIgnoreCase("ACK")) {
                    // 提取属性值
                    String aveError = root.getAttribute("AVE_ERROR");
                    String validPoints = root.getAttribute("VALID_POINTS");

                    // 打印属性值
                    System.out.println("AVE_ERROR: " + aveError);
                    System.out.println("VALID_POINTS: " + validPoints);

                    if (Integer.parseInt(validPoints) >= calibpts.size()){
                        return true;
                    }
                    return false;
                } else {
                    System.out.println("Invalid XML data: Root element is not <ACK>");
                }
            }
            outputStream.write("<SET ID=\"CALIBRATE_SHOW\" STATE=\"0\" />\r\n".getBytes());
            outputStream.flush();

            // 释放资源
            outputStream.close();
            input.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return false;
    }

    private static boolean waitForResponse(Socket socket) {
        try {
            socket.setSoTimeout(10000); // Set timeout to 10 seconds
            byte[] buffer = new byte[1024];
            int bytesRead = socket.getInputStream().read(buffer);
            return bytesRead != -1;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
