package airburn.fasmtel.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.function.Predicate;

public final class InsnNodePredicates {

	public static Predicate<AbstractInsnNode> Opcode(int opcode) {
		return n -> n.getOpcode() == opcode;
	}

	public static Predicate<AbstractInsnNode> Ldc(Object object) {
		return n -> n.getOpcode() == Opcodes.LDC && n instanceof LdcInsnNode && ((LdcInsnNode)n).cst.equals(object);
	}

	public static Predicate<AbstractInsnNode> Increment(int var) {
		return n -> n.getOpcode() == Opcodes.IINC && n instanceof IincInsnNode && ((IincInsnNode)n).var == var;
	}

	public static Predicate<AbstractInsnNode> Increment(int var, int incr) {
		return n -> n.getOpcode() == Opcodes.IINC && n instanceof IincInsnNode && ((IincInsnNode)n).var == var && ((IincInsnNode)n).incr == incr;
	}

	public static Predicate<AbstractInsnNode> Type(int opcode, String desc) {
		return n -> n.getOpcode() == opcode && n instanceof TypeInsnNode && ((TypeInsnNode)n).desc.equals(desc);
	}

	public static Predicate<AbstractInsnNode> Type(int opcode, Class<?> descClass) {
		return Type(opcode, ClassTransformerBase.getPath(descClass));
	}

	public static Predicate<AbstractInsnNode> Instanceof(String desc) {
		return Type(Opcodes.INSTANCEOF, desc);
	}

	public static Predicate<AbstractInsnNode> Instanceof(Class<?> descClass) {
		return Type(Opcodes.INSTANCEOF, descClass);
	}

	public static Predicate<AbstractInsnNode> Cast(String desc) {
		return Type(Opcodes.CHECKCAST, desc);
	}

	public static Predicate<AbstractInsnNode> Cast(Class<?> descClass) {
		return Type(Opcodes.CHECKCAST, descClass);
	}

	public static Predicate<AbstractInsnNode> Ctor(String owner) {
		return n -> {
			if(n.getOpcode() == Opcodes.INVOKESPECIAL && n instanceof MethodInsnNode) {
				MethodInsnNode m = ((MethodInsnNode)n);
				return m.owner.equals(owner) && m.name.equals("<init>");
			}
			return false;
		};
	}
	
	public static Predicate<AbstractInsnNode> Ctor(String owner, String desc) {
		return n -> {
			if(n.getOpcode() == Opcodes.INVOKESPECIAL && n instanceof MethodInsnNode) {
				MethodInsnNode m = ((MethodInsnNode)n);
				return m.owner.equals(owner) && m.name.equals("<init>") && m.desc.equals(desc);
			}
			return false;
		};
	}
	
	public static Predicate<AbstractInsnNode> Method(int opcode, String name) {
		return n -> n.getOpcode() == opcode && n instanceof MethodInsnNode && ((MethodInsnNode)n).name.equals(name);
	}

	public static Predicate<AbstractInsnNode> Method(int opcode, String owner, String name, String desc) {
		return n -> {
			if(n.getOpcode() == opcode && n instanceof MethodInsnNode) {
				MethodInsnNode m = ((MethodInsnNode)n);
				return m.owner.equals(owner) && m.name.equals(name) && m.desc.equals(desc);
			}
			return false;
		};
	}

	public static Predicate<AbstractInsnNode> MethodObf(int opcode, String obfName, String deobfName) {
		return n -> n.getOpcode() == opcode && n instanceof MethodInsnNode && (((MethodInsnNode)n).name.equals(obfName) || ((MethodInsnNode)n).name.equals(deobfName));
	}

	public static Predicate<AbstractInsnNode> MethodDesc(int opcode, String desc) {
		return n -> n.getOpcode() == opcode && n instanceof MethodInsnNode && ((MethodInsnNode)n).desc.equals(desc);
	}

	public static Predicate<AbstractInsnNode> MethodOwner(int opcode, String owner) {
		return n -> n.getOpcode() == opcode && n instanceof MethodInsnNode && ((MethodInsnNode)n).owner.equals(owner);
	}

	public static Predicate<AbstractInsnNode> Field(int opcode, String name) {
		return n -> n.getOpcode() == opcode && n instanceof FieldInsnNode && ((FieldInsnNode)n).name.equals(name);
	}

	public static Predicate<AbstractInsnNode> Field(int opcode, String owner, String name, String desc) {
		return n -> {
			if(n.getOpcode() == opcode && n instanceof FieldInsnNode) {
				FieldInsnNode f = ((FieldInsnNode)n);
				return f.owner.equals(owner) && f.name.equals(name) && f.desc.equals(desc);
			}
			return false;
		};
	}

	public static Predicate<AbstractInsnNode> FieldObf(int opcode, String obfName, String deobfName) {
		return n -> n.getOpcode() == opcode && n instanceof FieldInsnNode && (((FieldInsnNode)n).name.equals(obfName) || ((FieldInsnNode)n).name.equals(deobfName));
	}

	public static Predicate<AbstractInsnNode> FieldDesc(int opcode, String desc) {
		return n -> n.getOpcode() == opcode && n instanceof FieldInsnNode && ((FieldInsnNode)n).desc.equals(desc);
	}

	public static Predicate<AbstractInsnNode> FieldOwner(int opcode, String owner) {
		return n -> n.getOpcode() == opcode && n instanceof FieldInsnNode && ((FieldInsnNode)n).owner.equals(owner);
	}

	public static Predicate<AbstractInsnNode> Var(int opcode, int var) {
		return n -> n.getOpcode() == opcode && n instanceof VarInsnNode && ((VarInsnNode)n).var == var;
	}

}
