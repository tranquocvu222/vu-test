package jp.co.unixon.kousa.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "KOUSA_IRAI_TBL")
public class KousaIraiTbl implements Serializable  {

    /** hosoKyokuIdプロパティ */
    @Id
    @Column(precision = 10, nullable = false, unique = false)
    public Integer hosoKyokuId;

    /** kousaIraiNoプロパティ */
    @Id
    @Column(length = 20, nullable = false, unique = false)
    public String kousaIraiNo;

    /** kousaIraiSubNoプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer kousaIraiSubNo;

    /** kousaStateIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer kousaStateId;

    /** kousaPrivateModeプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public boolean kousaPrivateMode;

    /** kousaKenkaiIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer kousaKenkaiId;

    /*追加*/
    /** kousa2KenkaiIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer kousa2KenkaiId;

    /** kenkaiAttentionIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer kenkaiAttentionId;

    /** carrySozaiIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer carrySozaiId;

    /** carrySozaiプロパティ */
    @Column(length = 1023, nullable = true, unique = false)
    public String carrySozai;

    /** sozaiKindIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer sozaiKindId;

    /** advcoIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer advcoId;

    /** sponsorIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer sponsorId;

    /** areaIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer areaId;

    /** titleプロパティ */
    @Column(length = 255, nullable = false, unique = false)
    public String title;

    /** comment_eigyoプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String commentEigyo;

    /*追加*/
    /** comment_kousaプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String commentKousa;

    /** inputKenkaiTextプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String inputKenkaiText;

    /*追加*/
    /** inputKenkai2Textプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String inputKenkai2Text;

    /** kenkaiTextプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String kenkaiText;

    /** kousaMemoプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String kousaMemo;

    /** kokaiKenkaiプロパティ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean kokaiKenkai;

    /** blockKokaiKenkaiプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean blockKokaiKenkai;

    /** tantoUserIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer tantoUserId;

    /** tantoBusyoプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String tantoBusyo;

    /** iraiDateプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp iraiDate;

    /** kousaUserIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer kousaUserId;

    /** kousa2UserIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer kousa2UserId;

    /** kousaAtプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp kousaAt;

    /** deadlineDayプロパティ */
    @Column(nullable = false, unique = false)
    public Timestamp deadlineDay;

    /** kaitoIraiDateプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp kaitoIraiDate;

    /** hosoYoteiプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp hosoYotei;

    /** enableプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean enable;

    /** kousaJudgプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean kousaJudg;

    /** createdByプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer createdBy;

    /** createdAtプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp createdAt;

    /** updatedByプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer updatedBy;

    /** updatedAtプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp updatedAt;

    /** iraiMailSendDateプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp iraiMailSendDate;

    /** kaitoMailSendDateプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp kaitoMailSendDate;

    /** kakuninMailSendDateプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp kakuninMailSendDate;

    /** kaitoCompDateプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp kaitoCompDate;

    /** itemNameプロパティ */
    @Column(length = 255, nullable = true, unique = false)
    public String itemName;

    /** selectTeikeiプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean selectTeikei;

    /** kousaTantoUserIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer kousaTantoUserId;

    /** hosoKiboUmuプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean hosoKiboUmu;

    /** hosoKiboJikiプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String hosoKiboJiki;

    /** netLocalKbnプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer netLocalKbn;

    /** kousakenkaiOtherTextプロパティ */
    @Column(length = 255, nullable = true, unique = false)
    public String kousakenkaiOtherText;

    /*追加*/
    /** kousakenkai2OtherTextプロパティ */
    @Column(length = 255, nullable = true, unique = false)
    public String kousakenkai2OtherText;

    /** kenkaiAttentionOtherTextプロパティ */
    @Column(length = 255, nullable = true, unique = false)
    public String kenkaiAttentionOtherText;

    /*追加*/
    /** kenkai2AttentionOtherTextプロパティ */
    @Column(length = 255, nullable = true, unique = false)
    public String kenkai2AttentionOtherText;

    /** progModelプロパティ */
    @Column(length = 2, nullable = true, unique = false)
    public String progModel;

    /** itemNameModelプロパティ */
    @Column(length = 2, nullable = true, unique = false)
    public String itemNameModel;

    /*追加*/
    /** rowDeleteプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean rowDelete;

    /*追加*/
    /** cccCodeプロパティ */
    @Column(length = 6, nullable = true, unique = false)
    public String cccCode;

    /*追加*/
    /** sozaiCodeプロパティ */
    @Column(length = 8, nullable = true, unique = false)
    public String sozaiCode;

    /*追加*/
    /** gyousyuIdプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer gyousyuId;

    /*追加*/
    /** gyotaiKekkaプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean gyotaiKekka;

    /*追加*/
    /** henNameプロパティ */
    @Column(length = 255, nullable = true, unique = false)
    public String henName;

    /*追加*/
    /** henCountプロパティ */
    @Column(length = 2, nullable = true, unique = false)
    public String henCount;

    /*追加*/
    /** henseiKoukaKakuninプロパティ */
    @Column(length = 2, nullable = true, unique = false)
    public String henseiKoukaKakunin;

    /** iraiBusyoIdプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer iraiBusyoId;
}
