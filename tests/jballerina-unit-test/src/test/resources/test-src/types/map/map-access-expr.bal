function mapAccessTest(int x, int y) returns (int) {
    map<any> testMap = {};
    int xx = 0;
    int yy = 0;
    testMap["first"] = x;
    testMap["second"] = y;
    testMap["third"] = x + y;
    testMap["forth"] = x - y;
    xx = <int> testMap["first"];
    yy = <int> testMap["second"];

    return xx + yy;
}

function mapReturnTest(string firstName, string lastName) returns (map<any>) {
    map<any> testMap = {};
    testMap["fname"] = firstName;
    testMap["lname"] = lastName;
    testMap[ firstName + lastName ] = firstName + lastName;
    return testMap;
}

function testArrayAccessAsIndexOfMapt() returns (string) {
    map<any> namesMap = {fname:"Supun",lname:"Setunga"};
    string[] keys = ["fname","lname"];
    string key = "";
    var a = namesMap[keys[0]];
    key =  a is string ? a : "";
    return key;
}

function testAccessThroughVar() returns (string) {
    map<any> m = {};
    m["x"] = "a";
    m["y"] = "b";
    m["z"] = "c";
    return constructString(m);
}

function constructString(map<any> m) returns (string) {
    string [] keys = m.keys();
    int len = keys.length();
    int i = 0;
    string returnStr = "";
    while (i < len) {
        string key = keys[i];
        var a = m[key];
        string val = a is string ? a : "";
        returnStr = returnStr + key + ":" + val + ", ";
        i = i + 1;
    }
    return returnStr;
}

function testMapRemoveAll() returns (int) {
    map<any> namesMap = {fname:"Supun", lname:"Setunga", sname:"Kevin", tname:"Ratnasekera"};
    namesMap.removeAll();
    return namesMap.length();
}

function testHasKeyPositive() returns (boolean) {
    map<any> namesMap = {fname:"Supun", lname:"Setunga", sname:"Kevin", tname:"Ratnasekera"};
    return namesMap.hasKey("fname");
}

function testHasKeyNegative() returns (boolean) {
    map<any> namesMap = {fname:"Supun", lname:"Setunga", sname:"Kevin", tname:"Ratnasekera"};
    return namesMap.hasKey("fname2");
}

function testGetMapValues () returns [string, string] {
    json j = {"city":"Colombo", "country":"SriLanka"};
    int[] arr = [7,8,9];
    map<any> address = {city:"CA", "country":"USA"};
    map<map<any>>[] addressArray=[{address:{city:"Colombo", "country":"SriLanka"}},
                               {address:{city:"Kandy", "country":"SriLanka"}},
                               {address:{city:"Galle", "country":"SriLanka"}}];
    map<any> m = { name:"Supun",
                  age:25,
                  gpa:2.81,
                  status:true,
                  info:(),
                  address:address,
                  intArray:arr,
                  addressArray:addressArray,
                  finfo:j
                };
    any[] values = [];
    values = m.reduce(function(any[] arr, any value) returns any[] {
        arr[arr.length()] = value;
        return arr;
    }, values);
    var nam = <string> values[0];
    var jsn = <json> values[8];
    var city = <string> jsn.city;
    return [nam, city];
}

function testMapRemovePositive() returns [boolean, boolean, boolean] {
    map<any> namesMap = {fname:"Supun", lname:"Setunga", sname:"Kevin", tname:"Ratnasekera"};
    return [namesMap.hasKey("fname"), namesMap.remove("fname") === "Supun", namesMap.hasKey("fname")];
}

function testMapRemoveNegative() {
    map<any> namesMap = {fname:"Supun", lname:"Setunga", sname:"Kevin", tname:"Ratnasekera"};
    _ = namesMap.remove("fname2"); // panic
}

function testMapConcurrentAccess() returns int {
    map<int> intMap = {};
    int n = 100000;
    processConcurrent(intMap, n);
    return intMap.length();
}

function processConcurrent(map<int> intMap, int n) {
    worker w1 {
      int i = 0;
      int j;
      string k;
      while (i < n) {
         intMap["X"] = 100;
         j = intMap.get("X");
         k = i.toString();
         intMap[k] = i;
         i = intMap[k] ?: 0;  
         _ = intMap.remove(k);
         i += 1;
      }
    }
    worker w2 {
      int i = n;
      int j;
      string k;
      int n2 = n * 2;
      while (i < n2) {
         intMap["X"] = 200;
         j = intMap.get("X");
         k = i.toString();
         intMap[k] = i;
         i = intMap[k] ?: 0;
         _ = intMap.remove(k);
         i += 1;
      }
    }
    _ = wait {w1, w2};
}

function testConcurrentMapGetKeys() returns error? {
    map<int> intMap = {};
    int n = 100000;
    return processConcurrentKeys(intMap, n);
}

function processConcurrentKeys(map<int> intMap, int n) returns error? {
    worker w1 {
        int i = 0;
        int j;
        string k;
        while (i < n) {
            intMap["X"] = 100;
            j = intMap.get("X");
            k = i.toString();
            intMap[k] = i;
            i = intMap[k] ?: 0;  
            _ = intMap.remove(k);
            i += 1;
        }
    }
    worker w2 {
        int i = n;
        int j;
        string k;
        int n2 = n * 2;
        while (i < n2) {
            intMap["X"] = 200;
            j = intMap.get("X");
            k = i.toString();
            intMap[k] = i;
            i = intMap[k] ?: 0;
            _ = intMap.remove(k);
            i += 1;
        }
    }
    worker w3 {
        int i = 0;
        int length;
        while (i < n) {
            length = intMap.keys().length();
            i += 1;
        }
        return ();
    }
    _ = wait {w1, w2, w3};
    return ();
}
