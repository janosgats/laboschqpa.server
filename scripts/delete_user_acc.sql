delete
from granted_authority
where user_id IN(:userIds);

delete
from user_email_address
where user_id IN(:userIds);

delete gi, go
from external_account_detail ex
         left join google_external_account_detail go on ex.id = go.id
         left join github_external_account_detail gi on ex.id = gi.id
where ex.user_id IN(:userIds);

delete ex
from external_account_detail ex
where ex.user_id IN(:userIds);

delete
from user_acc
where id IN(:userIds);