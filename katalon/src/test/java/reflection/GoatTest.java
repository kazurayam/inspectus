package reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * https://www.baeldung.com/java-reflection
 */
public class GoatTest {

    @Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        Object goat = new Goat();
        Class<?> clazz = goat.getClass();
        assertEquals("Goat", clazz.getSimpleName());
        assertEquals("reflection.Goat", clazz.getName());
        assertEquals("reflection.Goat", clazz.getCanonicalName());
    }

    /**
     * 5.2 Class Names
     *
     * Fully Qualified ClassnameであるStringをパラメータとして
     * Class.forName()メソッドを呼ぶことによりClassオブジェクトを
     * 生成することができる。
     *
     * @throws ClassNotFoundException
     */
    @Test
    public void givenClassName_whenCreateObject_thenCorrect() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("reflection.Goat");
        assertEquals("Goat", clazz.getSimpleName());
        assertEquals("reflection.Goat", clazz.getName());
        assertEquals("reflection.Goat", clazz.getCanonicalName());
    }

    /**
     * 5.3 Class Modifier
     */
    @Test
    public void givenClass_wheRecognisesModifiers_thenCorrect() throws ClassNotFoundException {
        Class<?> goatClass = Class.forName("reflection.Goat");
        Class<?> animalClass = Class.forName("reflection.Animal");
        int goatMods = goatClass.getModifiers();
        int animalMods = animalClass.getModifiers();
        assertTrue(Modifier.isPublic(goatMods));
        assertTrue(Modifier.isAbstract(animalMods));
        assertTrue(Modifier.isPublic(animalMods));
    }

    /**
     * 5.4 Package Information
     */
    @Test
    public void giveClass_whenGetsPackageInfo_thenCorrect() {
        Goat goat = new Goat();
        Class<?> goatClass = goat.getClass();
        Package pkg = goatClass.getPackage();
        assertEquals("reflection", pkg.getName());
    }

    /**
     * 5.5 Superclass
     */
    @Test
    public void givenClass_whenGetsSuperClass_thenCorrect() {
        Goat goat = new Goat();
        String str = "any string";
        Class<?> goatClass = goat.getClass();
        Class<?> goatSuperClass = goatClass.getSuperclass();
        assertEquals("Animal", goatSuperClass.getSimpleName());
        assertEquals("Object", str.getClass().getSuperclass().getSimpleName());
    }

    /**
     * 5.6 Implemented Interfaces
     */
    public void givenClass_whenGetsImplementedInterfaces_thenCorrect() throws ClassNotFoundException {
        Class<?> goatClass = Class.forName("reflection.Goat");
        Class<?> animalClass = Class.forName("reflection.Animal");
        Class<?>[] goatInterfaces = goatClass.getInterfaces();
        Class<?>[] animalInterfaces = animalClass.getInterfaces();
        assertEquals(1, goatInterfaces.length);
        assertEquals(1, animalInterfaces.length);
        assertEquals("Locomotion", goatInterfaces[0].getSimpleName());
        assertEquals("Eating", animalInterfaces[0].getSimpleName());
    }

    /**
     * 5.7 Constructors, Methods and Fields
     */
    @Test
    public void givenClass_whenGetsConstructor_thenCorrect() throws ClassNotFoundException {
        Class<?> goatClass = Class.forName("reflection.Goat");
        Constructor<?>[] constructors = goatClass.getConstructors();
        assertEquals(1, constructors.length);
        assertEquals("reflection.Goat", constructors[0].getName());
    }

    @Test
    public void givenClass_whenGetsField_thenCorrect() {}

}
