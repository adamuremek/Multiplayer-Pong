
public class Matrix {

    int lenght;
    int height;
    int[][] matrix;

    Matrix(int length, int height) {
        this.lenght = length;
        this.height = height;
        matrix = new int[length][height];
    }

    public static void chainOrder(Matrix[] p) {
        int n = p.length - 1;
        int[][] m = new int[n][n]; 
        int[][] s = new int[n][n];
         
        for (int i = 0; i < n; i++) {
            m[i][i] = 1;
            
        }

        
        
    }

    public static void main(String[] args) {
        Matrix[] matrices = new Matrix[6];
        matrices[0] = new Matrix(30, 35);
        matrices[1] = new Matrix(35, 15);
        matrices[2] = new Matrix(15, 05);
        matrices[3] = new Matrix(05, 10);
        matrices[4] = new Matrix(10, 20);
        matrices[5] = new Matrix(20, 25);

        chainOrder(matrices);
    }

}
