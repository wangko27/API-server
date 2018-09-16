package io.nuls.api.server.dto.contract.vm;

public class ProgramMethodArg {

    private String type;

    private String name;

    private boolean required;

    public ProgramMethodArg() {
    }

    public ProgramMethodArg(String type, String name, boolean required) {
        this.type = type;
        this.name = name;
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramMethodArg that = (ProgramMethodArg) o;

        if (required != that.required) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    public boolean equalsNrc20(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramMethodArg that = (ProgramMethodArg) o;

        if (required != that.required) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProgramMethodArg{" +
                "type=" + type +
                ", name=" + name +
                ", required=" + required +
                '}';
    }

}
