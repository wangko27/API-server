package io.nuls.api.crypto.script;

import io.nuls.api.constant.NulsConstant;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.BaseNulsData;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.NulsOutputStreamBuffer;
import io.nuls.api.utils.SerializeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptSign extends BaseNulsData {

    private List<Script> scripts;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        if (scripts != null && scripts.size() > 0) {
            for (Script script : scripts) {
                stream.writeBytesWithLength(script.getProgram());
            }
        } else {
            stream.write(NulsConstant.PLACE_HOLDER);
        }
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        List<Script> scripts = new ArrayList<>();
        while (!byteBuffer.isFinished()) {
            scripts.add(new Script(byteBuffer.readByLengthByte()));
        }
        this.scripts = scripts;
    }

    public static ScriptSign createFromBytes(byte[] bytes) throws NulsException {
        ScriptSign sig = new ScriptSign();
        sig.parse(bytes, 0);
        return sig;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    @Override
    public int size() {
        int size = 0;
        if (scripts != null && scripts.size() > 0) {
            for (Script script : scripts) {
                size += SerializeUtils.sizeOfBytes(script.getProgram());
            }
        } else {
            size = 4;
        }
        return size;
    }

}
