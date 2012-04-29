package clear.transaction;

import java.util.List;

public interface IBatchGateway {
	 List<BatchMember> execute(List<BatchMember> items);
}
