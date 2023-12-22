package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {



    @Test
    void main() {
        String[] test = new String[1];
        test[0] = "test";
        assertDoesNotThrow(() -> Main.main(test));
    }
}