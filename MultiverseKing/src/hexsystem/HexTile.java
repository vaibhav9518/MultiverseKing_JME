package hexsystem;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede, Roah
 */
public class HexTile implements Savable {
    private byte element;
    private byte height;

    public HexTile(){
    }
    
    public HexTile(ElementalAttribut eAttribut) {
        this.element = (byte) eAttribut.ordinal();
        this.height = 0;
    }

    public HexTile(ElementalAttribut hexElement, byte height) {
        this.element = (byte)hexElement.ordinal();
        this.height = height;
    }

    public ElementalAttribut getElement() {
        return ElementalAttribut.convert(element);
    }

    public byte getHeight() {
        return height;
    }

    /**
     * Returns a clone of this tile with changed height
     *
     * @param height
     * @return
     */
    public HexTile cloneChangedHeight(byte height) {
        return new HexTile(ElementalAttribut.convert(element), (byte) (height));
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(height, "height", height);
        capsule.write(element, "element", element);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
//        capsule.readByte("height", height);
//        capsule.readByte("element", element);
        capsule.readInt("height", height);
        capsule.readInt("element", element);
    }
}
