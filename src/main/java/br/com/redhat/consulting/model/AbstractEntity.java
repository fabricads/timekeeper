package br.com.redhat.consulting.model;


public abstract class AbstractEntity implements IEntity {

    private static final long serialVersionUID = 1L;

    private Integer id;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int hashCode() {
        Object id = this.id;
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? super.hashCode() : id.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        
        if (obj instanceof AbstractEntity) {
            final AbstractEntity other = (AbstractEntity) obj;
            Object id = this.id;
            if (id == null) {
                if (other.getId() != null)
                    return false;
            } else if (!id.equals(other.getId())) {
                return false;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getName()).append("[id = ").append(getId()).append(" ]");
        return s.toString();
    }
    
}
