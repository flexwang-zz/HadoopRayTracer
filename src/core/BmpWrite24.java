package core;


class BmpWrite24 {
	public static byte[] bmphead(int width, int height) {
		byte[] bfhead = new byte[58];

		int bfType = 0x424d; 
		int bfSize = 54 + width * height * 3;
		int bfReserved1 = 0;
		int bfReserved2 = 0;
		int bfOffBits = 54;


		bfhead[0] = 0x42;
		bfhead[1] = 0x4d;

		System.arraycopy(changeByte(bfSize), 0, bfhead, 2, 4);
		System.arraycopy(changeByte(bfReserved1), 0, bfhead, 6, 4);
		System.arraycopy(changeByte(bfReserved2), 0, bfhead, 8, 4);
		System.arraycopy(changeByte(bfOffBits), 0, bfhead, 10, 4);

		int biSize = 40;
		int biWidth = width;
		int biHeight = height;
		int biPlanes = 1;
		int biBitcount = 24;
		int biCompression = 0;
		int biSizeImage = width * height;
		int biXPelsPerMeter = 0;
		int biYPelsPerMeter = 0;
		int biClrUsed = 0;
		int biClrImportant = 0;


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