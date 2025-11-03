public class MainCalc{
    public static void main(String[] args) {
        System.out.println("Start: multimethods calling from now on.");
        
        // define a and b.
        int a = 10;
        int b = 11;

        // call the results.
        int resultOfPlus = CalcLogics.plusCalc(a, b);
        int resultOfMinus = CalcLogics.minusCalc(a, b);

        // display the results.
        System.out.println("**********************************************************");
        System.out.println("The Plus Answer is " + resultOfPlus + ". ");
        System.out.println("The Minus Answer is " + resultOfMinus + ". ");
        System.out.println("**********************************************************");

        System.out.println("End: multimethods ended");

    } 
}