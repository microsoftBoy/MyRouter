package com.example.shuaige.myrouter.compiler.utils;

import com.example.shuaige.myrouter.facade.enums.TypeKind;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.example.shuaige.myrouter.compiler.utils.Consts.BOOLEAN;
import static com.example.shuaige.myrouter.compiler.utils.Consts.BYTE;
import static com.example.shuaige.myrouter.compiler.utils.Consts.INTEGER;
import static com.example.shuaige.myrouter.compiler.utils.Consts.PARCELABLE;
import static com.example.shuaige.myrouter.compiler.utils.Consts.SERIALIZABLE;
import static com.example.shuaige.myrouter.compiler.utils.Consts.SHORT;
import static com.example.shuaige.myrouter.compiler.utils.Consts.LONG;
import static com.example.shuaige.myrouter.compiler.utils.Consts.DOUBEL;
import static com.example.shuaige.myrouter.compiler.utils.Consts.FLOAT;
import static com.example.shuaige.myrouter.compiler.utils.Consts.CHAR;
import static com.example.shuaige.myrouter.compiler.utils.Consts.STRING;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public class TypeUtils {
    private Types types;
    private Elements elements;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;
        parcelableType = this.elements.getTypeElement(PARCELABLE).asType();
        serializableType = this.elements.getTypeElement(SERIALIZABLE).asType();
    }

    /**
     * 判断出真正的Java类型
     * @param element 原始的类型
     * @return Java类
     */
    public int typeExchange(Element element){
        TypeMirror typeMirror = element.asType();
        //TODO what?
        if (typeMirror.getKind().isPrimitive()){
            //java.lang.Enum.ordinal() 方法返回枚举常量的序数(它在枚举声明，其中初始常量分配的零序位)
            return element.asType().getKind().ordinal();
        }
        switch (typeMirror.toString()){
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                if (types.isSubtype(typeMirror,parcelableType)) {
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror,serializableType)){
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
    /**
     * public enum  Mobile {
     Meizu(100), Huwwei(200),Xiaomi(300);
     }
     对应的ordinal 分别是 0,1,2
     */
}
