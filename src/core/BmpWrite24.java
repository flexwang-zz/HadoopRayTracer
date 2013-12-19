package core;


class BmpWrite24 {
	public static byte[] bmphead(int width, int height) {
		byte[] bfhead = new byte[58];
		// 给文件头的变量赋值
		int bfType = 0x424d; // 位图文件类型（0—1字节）
		int bfSize = 54 + width * height * 3;// bmp文件的大小（2—5字节）
		int bfReserved1 = 0;// 位图文件保留字，必须为0（6-7字节）
		int bfReserved2 = 0;// 位图文件保留字，必须为0（8-9字节）
		int bfOffBits = 54;// 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）

		// 输入数据的时候要注意输入的数据在内存中要占几个字节，
		// 然后再选择相应的写入方法，而不是它自己本身的数据类型
		// 输入文件头数据
		bfhead[0] = 0x42;
		bfhead[1] = 0x4d;

		System.arraycopy(changeByte(bfSize), 0, bfhead, 2, 4);
		System.arraycopy(changeByte(bfReserved1), 0, bfhead, 6, 4);
		System.arraycopy(changeByte(bfReserved2), 0, bfhead, 8, 4);
		System.arraycopy(changeByte(bfOffBits), 0, bfhead, 10, 4);

		// 给信息头的变量赋值
		int biSize = 40;// 信息头所需的字节数（14-17字节）
		int biWidth = width;// 位图的宽（18-21字节）
		int biHeight = height;// 位图的高（22-25字节）
		int biPlanes = 1; // 目标设备的级别，必须是1（26-27字节）
		int biBitcount = 24;// 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。
		int biCompression = 0;// 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。
		int biSizeImage = width * height;// 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）
		int biXPelsPerMeter = 0;// 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值
		int biYPelsPerMeter = 0;// 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值
		int biClrUsed = 0;// 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了
		int biClrImportant = 0;// 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要

		// 因为java是大端存储，那么也就是说同样会大端输出。
		// 但计算机是按小端读取，如果我们不改变多字节数据的顺序的话，那么机器就不能正常读取。
		// 所以首先调用方法将int数据转变为多个byte数据，并且按小端存储的顺序。

		// 输入信息头数据
		System.arraycopy(changeByte(biSize), 0, bfhead, 14, 4);
		System.arraycopy(changeByte(biWidth), 0, bfhead, 18, 4);
		System.arraycopy(changeByte(biHeight), 0, bfhead, 22, 4);
		System.arraycopy(changeByte(biPlanes), 0, bfhead, 26, 4);
		System.arraycopy(changeByte(biBitcount), 0, bfhead, 28, 4);
		System.arraycopy(changeByte(biCompression), 0, bfhead, 30, 4);
		System.arraycopy(changeByte(biSizeImage), 0, bfhead, 34, 4);
		System.arraycopy(changeByte(biXPelsPerMeter), 0, bfhead, 38, 4);
		System.arraycopy(changeByte(biYPelsPerMeter), 0, bfhead, 42, 4);
		System.arraycopy(changeByte(biClrUsed), 0, bfhead, 46, 4);
		System.arraycopy(changeByte(biClrImportant), 0, bfhead, 50, 4);

		return bfhead;
	}

	public static byte[] changeByte(int data) {
		byte b4 = (byte) ((data) >> 24);
		byte b3 = (byte) (((data) << 8) >> 24);
		byte b2 = (byte) (((data) << 16) >> 24);
		byte b1 = (byte) (((data) << 24) >> 24);
		byte[] bytes = { b1, b2, b3, b4 };
		return bytes;
	}

	public static void main(String[] args) {
		bmphead(200, 200);
	}
}