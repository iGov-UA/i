update_act_hi_detail;update "public"."act_hi_detail" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%' and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_detail;delete from "public"."act_hi_detail" d where "proc_inst_id_" = '0'
update_act_hi_varinst;update "public"."act_hi_varinst" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%' and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_varinst;delete from "public"."act_hi_varinst" d where "proc_inst_id_" = '0'
update_act_hi_attachment;update "public"."act_hi_attachment" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%'  and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_attachment;delete from "public"."act_hi_attachment" d where "proc_inst_id_" = '0'
update_act_hi_comment;update "public"."act_hi_comment" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%' and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_comment;delete from "public"."act_hi_comment" d where "proc_inst_id_" = '0'
update_act_hi_identitylink;update "public"."act_hi_identitylink" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%' and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_identitylink;delete from "public"."act_hi_identitylink" d where "proc_inst_id_" = '0'
update_act_hi_actinst;update "public"."act_hi_actinst" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%' and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_actinst;delete from "public"."act_hi_actinst" d where "proc_inst_id_" = '0'
update_act_hi_taskinst;update "public"."act_hi_taskinst" d set "proc_inst_id_" = '0' from "public"."act_hi_procinst" p where p."proc_def_id_" like '%s%' and p."proc_inst_id_" = d."proc_inst_id_"
delete_act_hi_taskinst;delete from "public"."act_hi_taskinst" d where "proc_inst_id_" = '0'
delete_act_hi_procinst;delete from "public"."act_hi_procinst" where "proc_def_id_" like '%s%'   