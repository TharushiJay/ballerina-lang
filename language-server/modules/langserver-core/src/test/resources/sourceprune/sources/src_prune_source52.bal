import module1;

function getStringInPkg() returns (string){
    int a = 12;
    
    worker name returns module1: {
        printMessage();
    }
    
    int b = 12;
}
