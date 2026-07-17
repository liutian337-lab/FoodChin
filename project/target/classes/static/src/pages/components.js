
// 头部组件
const Header = {
  props: ['login', 'user'],
  template: `
    <div class="header">
      <h3>食品溯源平台</h3>
      <span v-if="login" class="user-name">{{ user }}</span>
    </div>
  `
}

// 食品详情展示
const FoodDetail = {
  props: ['food'],
  computed: {
    date() {
      if (this.food.timestamp) return dateTime(this.food.timestamp)
    }
  },
  template: `
    <div class="fooddetail">
      <h4>食品基本信息</h4>
      <div class="content trace-content">
        <div>
          <span>农场地址</span>
          <span>{{ food.from_address || food.address }}</span>
        </div>
        <div>
          <span>溯源码</span>
          <span>{{ food.traceNumber }}</span>
        </div>
        <div>
          <span>食品名称</span>
          <span>{{ food.name }}</span>
        </div>
        <div>
          <span>生产商</span>
          <span>{{ food.from || food.produce }}</span>
        </div>
        <div>
          <span>采摘时间</span>
          <span>{{ date }}</span>
        </div>
      </div>
    </div>
  `
}

// 溯源详情展示
const TraceDetail = {
  components: {
    FoodDetail
  },
  props: ['foodDetail', 'user'],
  template: `
    <div>
      <h3 class="trace-title">以下是溯源码为{{foodDetail[0].traceNumber}}的溯源信息</h3>
      <div class="tracedetail">
        <FoodDetail :food="foodDetail[0]" />

        <el-divider v-if="foodDetail[1] && (user === 2 || user === 3)"></el-divider>
        <div v-if="foodDetail[1] && (user === 2 || user === 3)">
          <h4>流程信息</h4>
          <div class="trace-timeline trace-content">
            <el-timeline>
              <el-timeline-item class="el-timeline-no-node el-timeline-no-tail">
                <div>
                  <span>生产/加工商地址</span>
                  <span>{{ foodDetail[1].to_address }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item>
                <div>
                  <span>入库时间</span>
                  <span>{{ dateTime(foodDetail[1].timestamp) }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item>
                <div>
                  <span>质检情况</span>
                  <span>{{ foodDetail[1].quality === 0 ? '优质' : foodDetail[1].quality === 1 ? '合格' : '不合格' }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item>
                <div>
                  <span>发货单位</span>
                  <span>{{ foodDetail[1].from }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item style="height:60px;" class="el-timeline-no-tail">
                <div>
                  <span>收货单位</span>
                  <span>{{ foodDetail[1].to }}</span>
                </div>
              </el-timeline-item>

              <el-timeline-item class="el-timeline-no-node el-timeline-no-tail" v-if="foodDetail[2] && user === 3">
                <div>
                  <span>零售商地址</span>
                  <span>{{ foodDetail[2].to_address }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="foodDetail[2] && user === 3">
                <div>
                  <span>入库时间</span>
                  <span>{{ dateTime(foodDetail[2].timestamp) }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="foodDetail[2] && user === 3">
                <div>
                  <span>质检情况</span>
                  <span>{{ foodDetail[2].quality === 0 ? '优质' : foodDetail[2].quality === 1 ? '合格' : '不合格' }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="foodDetail[2] && user === 3">
                <div>
                  <span>发货单位</span>
                  <span>{{ foodDetail[2].from }}</span>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="foodDetail[2] && user === 3">
                <div>
                  <span>收货单位</span>
                  <span>{{ foodDetail[2].to }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
    </div>
  `,
  methods: {
    dateTime(val) {
      return dateTime(val)
    }
  }
}

// 添加食品组件
const AddVegetable = {
  components: {
    TraceDetail
  },
  data() {
    return {
      form: {
        traceName: '',
        quality: '',
      },
      formLabelWidth: '180px',
    }
  },
  props: ['dialogFormVisible', 'food', 'user'],
  template: `
    <div>
      <el-dialog title="添加食品信息" :visible.sync="dialogFormVisible" :show-close="false" width="40%" :center="true">
        <el-form :model="form" ref="form" label-position="left" :label-width="formLabelWidth">
          <el-form-item 
            label="质检情况"
            prop="quality"
            :rules="[
              { required: true, message: '请选择质检情况', trigger: 'change' }
            ]">
            <el-radio-group v-model="form.quality">
              <el-radio label="0">优质</el-radio>
              <el-radio label="1">合格</el-radio>
              <el-radio label="2">不合格</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="收货单位(本单位)"
            prop="traceName"
            :rules="[
              { required: true, message: '请输入收货单位' }
            ]">
            <el-input v-model="form.traceName" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div>
          <el-button @click="$emit('popup', false)">取 消</el-button>
          <el-button type="primary" @click="submitForm('form')">确 定</el-button>
        </div>
        <el-divider></el-divider>

        <TraceDetail :food-detail="food" :user="user" />
      </el-dialog>
    </div>
  `,
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$emit('confirmPopup', this.form)
        } else {
          return false;
        }
      });
    }
  }
}

// mixin， 这里主要处理生产/加工商和超市的耦合业务
const mixin = {
  components: {
    AddVegetable,
    FoodDetail
  },
  props: ['currPage', 'pageSize'],
  data() {
    return {
      popup: false,
      traceNumber: '',
      foodDetail: '',
      foodList: [],
      popoverData: {},
      _validating: false,
    }
  },
  computed: {
    renderList() {
      return this.foodList.slice((this.currPage - 1) * this.pageSize, this.currPage * this.pageSize)
    }
  },
  template: `
    <div>
      <el-input
        placeholder="请输入溯源码"
        v-model.number="traceNumber"
        @input="validateTraceNumber"
        clearable>
      </el-input>
      <el-button type="primary" @click="addVegetable">添加食品信息</el-button>
      <el-button type="danger" @click="$emit('logout', false)" class="button-logout">退出</el-button>

      <el-table
        :data="renderList"
        style="width: 100%"
        :default-sort="{prop: 'date', order: 'descending'}"
      >
        <el-table-column prop="traceNumber" label="溯源码">
          <template slot-scope="scope">
            <el-popover trigger="click" placement="top" @show="handlePopover(scope.row)" :width="400">
              <FoodDetail :food="popoverData" />
              <div slot="reference">
                <span style="color: blue; cursor: pointer;">{{ scope.row.traceNumber }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="食品名称"></el-table-column>
        <el-table-column prop="date" label="上架时间" sortable></el-table-column>
        <el-table-column prop="quality" label="质检情况"></el-table-column>
        <el-table-column prop="from" label="发货单位"></el-table-column>
        <el-table-column prop="to" label="收获单位"></el-table-column>
      </el-table>

      <AddVegetable :dialogFormVisible="popup" :user="user" :food="foodDetail" @popup="handlePopup" @confirmPopup="formSubmit" />
    </div>
  `,
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      let pathname = this.user === 1 ? 'distributing' : 'retailing';
      axios({
        method: 'get',
        url: `/${pathname}`
      })
      .then(ret => {
        this.foodList = ret.data
          .map(item => {
            let quality = item.quality === 0 ? '优质' : item.quality === 1 ? '合格' : '不合格';
            return {
              ...item,
              date: dateTime(item.timestamp),
              quality,
            };
          })
          .reverse();
        this.$emit('total', this.foodList.length);
      })
      .catch(err => {
        console.log(err);
      });
    },

    validateTraceNumber() {
      if (this._validating) return;

      const value = this.traceNumber;

      if (value === '' || value === null || value === undefined) {
        return;
      }

      if (!/^\d+$/.test(String(value))) {
        this._validating = true;
        this.$message.warning('溯源码只能输入正整数');
        this.traceNumber = '';
        setTimeout(() => { this._validating = false; }, 300);
        return;
      }

      const num = Number(value);
      if (num <= 0) {
        this._validating = true;
        this.$message.warning('溯源码必须大于0');
        this.traceNumber = '';
        setTimeout(() => { this._validating = false; }, 300);
      } else if (num > 2147483647) {
        this._validating = true;
        this.$message.warning('溯源码不能超过2147483647');
        this.traceNumber = '';
        setTimeout(() => { this._validating = false; }, 300);
      }
    },

    handlePopover(data) {
      if (this.user === 1) {
        this.popoverData = data;
        return;
      }
      axios({
        method: 'get',
        url: `/food?traceNumber=${data.traceNumber}`
      })
      .then(ret => {
        this.popoverData = ret.data;
      })
      .catch(err => {
        console.log(err);
      });
    },

    addVegetable() {
      const value = this.traceNumber;

      if (value === '' || value === null || value === undefined) {
        this.$message.warning('请输入溯源码');
        return;
      }
      if (!/^\d+$/.test(String(value))) {
        this.$message.warning('溯源码只能输入正整数');
        this.traceNumber = '';
        return;
      }
      const num = Number(value);
      if (num <= 0) {
        this.$message.warning('溯源码必须大于0');
        this.traceNumber = '';
        return;
      }
      if (num > 2147483647) {
        this.$message.warning('溯源码不能超过2147483647');
        this.traceNumber = '';
        return;
      }

      axios({
        method: 'get',
        url: `/trace?traceNumber=${this.traceNumber}`
      })
      .then(ret => {
        this.foodDetail = ret.data || [];
        this.popup = true;
      })
      .catch(err => {
        console.log(err);
      });
    },

    handlePopup(val) {
      this.popup = val;
    },

    formSubmit(val) {
      if (!this.foodDetail || this.foodDetail.length === 0) {
        this.$message.error('请先查询溯源码');
        this.popup = false;
        return;
      }

      const traceNumber = this.foodDetail[0].traceNumber;
      if (!traceNumber) {
        this.$message.error('溯源码不存在，请重新查询');
        this.popup = false;
        return;
      }

      let pathname = this.user === 1 ? 'adddistribution' : 'addretail';
      axios({
        method: 'post',
        url: `/${pathname}`,
        data: {
          ...val,
          quality: parseInt(val.quality),
          traceNumber: traceNumber
        }
      })
      .then(ret => {
        if (ret.data.ret !== 1) {
          this.$message({
            message: '提交失败',
            type: 'error',
            center: true
          });
          return;
        }
        this.$message({
          message: '提交成功',
          type: 'success',
          center: true
        });
        this.getData();
      })
      .catch(err => {
        console.log(err);
      });
      this.popup = false;
    }
  }
}

// 创建食品组件
const CreateVegetable = {
  data() {
    return {
      form: {
        foodName: '',
        traceName: '',
        traceNumber: '',
        quality: '',
      },
      formLabelWidth: '120px',
      _validating: false
    }
  },
  props: ['dialogFormVisible'],
  template: `
    <div>
      <el-dialog title="新建食品" :visible.sync="dialogFormVisible" :show-close="false" width="35%" :center="true">
        <el-form :model="form" ref="form" label-position="left" :label-width="formLabelWidth">
          <el-form-item
            label="溯源码"
            prop="traceNumber"
            :rules="[
              { required: true, message: '请输入溯源码' }
            ]"
          >
            <el-input v-model.number="form.traceNumber" @input="validateTraceNumber" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item
            label="食品名称"
            prop="foodName"
            :rules="[
              { required: true, message: '请输入食品名称' }
            ]"
          >
            <el-input v-model="form.foodName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item
            label="生产商"
            prop="traceName"
            :rules="[
              { required: true, message: '请输入生产商' }
            ]"
          >
            <el-input v-model="form.traceName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item
            label="质检情况"
            prop="quality"
            :rules="[
              { required: true, message: '请选择质检情况', trigger: 'change' }
            ]"
          >
            <el-radio-group v-model="form.quality">
              <el-radio label="0">优质</el-radio>
              <el-radio label="1">合格</el-radio>
              <el-radio label="2">不合格</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="$emit('popup', false)">取 消</el-button>
          <el-button type="primary" @click="submitForm('form')">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  `,
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$emit('confirmPopup', this.form)
        } else {
          return false;
        }
      });
    },
    validateTraceNumber() {
      if (this._validating) return;

      const value = this.form.traceNumber;

      if (value === '' || value === null || value === undefined) {
        return;
      }

      if (!/^\d+$/.test(String(value))) {
        this._validating = true;
        this.$message.warning('溯源码只能输入正整数');
        this.form.traceNumber = '';
        setTimeout(() => { this._validating = false; }, 300);
        return;
      }

      const num = Number(value);
      if (num <= 0) {
        this._validating = true;
        this.$message.warning('溯源码必须大于0');
        this.form.traceNumber = '';
        setTimeout(() => { this._validating = false; }, 300);
      } else if (num > 2147483647) {
        this._validating = true;
        this.$message.warning('溯源码不能超过2147483647');
        this.form.traceNumber = '';
        setTimeout(() => { this._validating = false; }, 300);
      }
    }
  }
}